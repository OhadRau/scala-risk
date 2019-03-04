package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

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

sealed trait MapType {
  val filename: String
}

case object DefaultMap extends MapType {
  val filename: String = "default"
}

case class Map(territories: Seq[Territory] = new ArrayBuffer[Territory]()) {}

object Map {
  val logger = play.api.Logger(getClass)

  def loadFromFile(map: MapType): Option[Map] = {
    val bufferedSource = Source.fromFile(s"territoryConfiguration/${map.filename}.json")
    val configurationString = bufferedSource.getLines.mkString
    bufferedSource.close()
    val json: JsValue = Json.parse(configurationString)
    json.validate[MapConfiguration] match {
      case s: JsSuccess[MapConfiguration] => {
        val configuration: MapConfiguration = s.get
        createMapFromConfiguration(configuration)
      }
      case e: JsError =>
        logger.error(s"Error parsing ${map.filename}.json")
        None
    }
  }

  def createMapFromConfiguration(configuration: MapConfiguration): Option[Map] = {
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
    Some(Map(territories))
  }

  implicit val mapFormat: Writes[Map] = Json.writes[Map]
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
