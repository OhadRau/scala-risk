package models

import actors.ClientWithActor
import play.api.libs.json.{Json, OFormat}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import scala.util.Random

class Room(roomName: String, var host: ClientWithActor) {
  val clients: mutable.HashMap[String, ClientWithActor] = collection.mutable.HashMap[String, ClientWithActor]()
  val roomId: String = Random.alphanumeric take 16 mkString

  def getStatus: RoomStatus = {
    val names = ArrayBuffer[String]()
    for ((_, client) <- clients) {
      names += client.client.name
    }
    RoomStatus(names)
  }

  def getBrief: RoomBrief = {
    RoomBrief(roomName, host.client.name, roomId, clients.size)
  }

  def addClient(client: ClientWithActor): Unit = {
    clients += (client.client.token -> client)
  }
}

case class RoomStatus(clients: Seq[String])
case class RoomBrief(name: String, hostName: String, roomId: String, clientCount: Int)

object RoomStatus {
  implicit val roomStatusFormat: OFormat[RoomStatus] = Json.format[RoomStatus]
}

object RoomBrief {
  implicit val roomBriefFormat: OFormat[RoomBrief] = Json.format[RoomBrief]
}
