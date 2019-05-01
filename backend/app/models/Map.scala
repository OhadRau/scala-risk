package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.FiniteDuration
import scala.io.Source
import scala.xml._
import scala.concurrent.duration._

class Territory(val id: Int, maxCapacity: Int, var ownerToken: String = "", var armies: Int = 0,
                val neighbours: ArrayBuffer[Territory] = new ArrayBuffer[Territory]()) {

}

object Territory {
  implicit val territoryFormat: Writes[Territory] = (territory: Territory) => Json.obj(
    "id" -> territory.id,
    "ownerToken" -> territory.ownerToken,
    "armies" -> territory.armies,
    "neighbours" -> territory.neighbours.map(_.id)
  )
}

case class Map(territories: Seq[Territory] = new ArrayBuffer[Territory](), interval: FiniteDuration, resource: MapResource) {}

object Map {
  val logger = play.api.Logger(getClass)

  def loadFromFile(map: String): Either[String, Map] =
    for {
      config <- getConfiguration(s"territoryConfiguration/$map/graph.json")
      svg <- getSVG(s"territoryConfiguration/$map/map.svg",
        config.vertices.map(v => v.name),
        config.vertices.map(v => v.id),
        config.vertices.map(v => v.labelId))
    } yield createMapFromConfiguration(config, svg)

  def getSVG(path: String, names: Seq[String], states: Seq[String], stateLabels: Seq[String]): Either[String, MapResource] = {
    try {
      def getByIdList(svg: Elem, list: Seq[String]): Seq[String] =
        svg \\ "_" filter (node => list contains (node \ "@id").text) map (_.mkString)
      val svg = XML.loadFile(path)
      val svgGroups = getByIdList(svg, states)
      val labelGroups = getByIdList(svg, stateLabels)
      val labelPaths = svg \\ "_" find (node => (node \ "@id").text == "textPaths") map (_.mkString) getOrElse ""
      val viewBox = (svg \ "@viewBox").toString
      val style = (svg \\ "style").toString
      Right(MapResource(viewBox, style, names, svgGroups, labelGroups, labelPaths))
    } catch {
      case _: SAXParseException =>
        Left(s"Couldn't parse SVG for map at $path")
      case _: MalformedAttributeException =>
        Left(s"Malformed attribute in SVG for map at $path")
    }
  }

  def getConfiguration(path: String): Either[String, MapConfiguration] = {
    val bufferedSource = Source.fromFile(path)
    val configurationString = bufferedSource.getLines.mkString
    bufferedSource.close()
    val json: JsValue = Json.parse(configurationString)
    json.validate[MapConfiguration] match {
      case s: JsSuccess[MapConfiguration] =>
        Right(s.get)
      case _: JsError =>
        Left(s"Error parsing $path.json")
    }
  }

  def createMapFromConfiguration(configuration: MapConfiguration, mapResource: MapResource): Map = {
    logger.info(configuration.toString)
    val nameToIndex = mutable.HashMap[String, Int]()
    val territories: Array[Territory] = configuration.vertices.toArray.zipWithIndex.map {
      case (vertex, index) =>
        nameToIndex += (vertex.name -> index)
        new Territory(index, vertex.maxCapacity)
    }

    configuration.edges foreach (edge => {
      val from = territories(nameToIndex(edge.from))
      val to = territories(nameToIndex(edge.to))
      from.neighbours += to
      to.neighbours += from
    })
    Map(territories, configuration.interval, mapResource)
  }

  def unapply(arg: Map): Option[Seq[Territory]] = Some(arg.territories)

  implicit val mapWrites: Writes[Map] = (map: Map) => Json.obj(
    "territories" -> map.territories
  )
}

case class MapResource(viewBox: String, style: String, names: Seq[String], territories: Seq[String],
                       labels: Seq[String], labelPaths: String)

object MapResource {
  implicit val mapResourceWrite: Writes[MapResource] = Json.writes[MapResource]
}

case class Vertex(name: String, id: String, labelId: String, maxCapacity: Int)

case class Edge(from: String, to: String)

case class MapConfiguration(vertices: Seq[Vertex], edges: Seq[Edge], interval: FiniteDuration)

object MapConfiguration {
  implicit val vertexRead: Reads[Vertex] = (
    (JsPath \ 0).read[String] and
      (JsPath \ 1).read[String] and
      (JsPath \ 2).read[String] and
      (JsPath \ 3).read[Int]
    ) (Vertex.apply _)
  implicit val edgeRead: Reads[Edge] = (
    (JsPath \ 0).read[String] and
      (JsPath \ 1).read[String]
    ) (Edge.apply _)
  implicit val finiteDurationRead: Reads[FiniteDuration] = Reads.of[Long].map(FiniteDuration(_, MILLISECONDS))
  implicit val mapConfigurationRead: Reads[MapConfiguration] = (
    (JsPath \ "vertices").read[Seq[Vertex]] and
      (JsPath \ "edges").read[Seq[Edge]] and
      (JsPath \ "interval").read[FiniteDuration]
    ) (MapConfiguration.apply _)
}
