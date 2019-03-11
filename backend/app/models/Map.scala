package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.xml._

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

case class Map(territories: Seq[Territory] = new ArrayBuffer[Territory](), resource: MapResource) {}

object Map {
  val logger = play.api.Logger(getClass)

  def loadFromFile(map: String): Either[String, Map] =
    for {
      config <- getConfiguration(s"territoryConfiguration/${map}/graph.json")
      svg <- getSVG(s"territoryConfiguration/${map}/map.svg")
    } yield createMapFromConfiguration(config, svg)

  def getSVG(path: String): Either[String, MapResource] = {
    try {
      val svg = XML.loadFile(path)
      val svgGroups: Seq[String] = (svg \ "g") map (_.mkString)
      val viewBox = (svg \ "@viewBox").toString
      Right(MapResource(viewBox, svgGroups))
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
    Map(territories, mapResource)
  }

  def unapply(arg: Map): Option[Seq[Territory]] = Some(arg.territories)

  implicit val mapWrites: Writes[Map] = (map: Map) => Json.obj(
    "territories" -> map.territories
  )
}

case class MapResource(viewBox: String, territories: Seq[String])

object MapResource {
  implicit val mapResourceWrite: Writes[MapResource] = Json.writes[MapResource]
}

case class Vertex(name: String, maxCapacity: Int)

case class Edge(from: String, to: String)

case class MapConfiguration(vertices: Seq[Vertex], edges: Seq[Edge])

object MapConfiguration {
  implicit val vertexRead: Reads[Vertex] = (
    (JsPath \ 0).read[String] and
      (JsPath \ 1).read[Int]
    ) (Vertex.apply _)
  implicit val edgeRead: Reads[Edge] = (
    (JsPath \ 0).read[String] and
      (JsPath \ 1).read[String]
    ) (Edge.apply _)
  implicit val mapConfigurationRead: Reads[MapConfiguration] = (
    (JsPath \ "vertices").read[Seq[Vertex]] and
      (JsPath \ "edges").read[Seq[Edge]]
    ) (MapConfiguration.apply _)
}