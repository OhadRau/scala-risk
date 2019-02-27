package models

import actors.{ClientWithActor, JoinedRoom}
import play.api.libs.json.Json

import scala.collection.mutable
import scala.language.postfixOps
import scala.util.Random

sealed trait Status
case class Waiting(status: String = "Waiting") extends Status
case class Ready(status: String = "Ready") extends Status

class Room(roomName: String, var host: ClientWithActor) {
  val clients: mutable.HashMap[String, ClientWithActor] = collection.mutable.HashMap[String, ClientWithActor]()
  val statuses: mutable.HashMap[String, Status] = collection.mutable.HashMap[String, Status]()
  val roomId: String = Random.alphanumeric take 16 mkString

  def getStatus: RoomStatus = {
    RoomStatus(roomName, host.client.name getOrElse "", roomId, clients.values map (client =>
      ClientStatus(client.client.name getOrElse "", statuses(client.client.token), client.client.publicToken)) toSeq)
  }

  def setReady(token: String): Unit = {
    statuses(token) = Ready()
  }

  def getBrief: RoomBrief = RoomBrief(roomName, host.client.publicToken, roomId, clients.size)

  def addClient(client: ClientWithActor): Unit = {
    clients += client.client.token -> client
    statuses += client.client.token -> Waiting()
    val joinedMessage= JoinedRoom(roomId, client.client.publicToken)
    clients.values.foreach(_.actor ! joinedMessage)
  }
}

case class RoomStatus(name: String, hostName: String, roomId: String, clientStatus: Seq[ClientStatus])

case class RoomBrief(name: String, hostToken: String, roomId: String, numClients: Int)

case class ClientStatus(name: String, status: Status = Waiting(), publicToken: String)

object Status {
  implicit val waitingWrite = Json.writes[Waiting]
  implicit val readyWrite = Json.writes[Ready]
  implicit val statusWrite = Json.writes[Status]
}

object ClientStatus {
  implicit val clientStatusFormat = Json.writes[ClientStatus]
}

object RoomBrief {
  implicit val roomBriefFormat = Json.writes[RoomBrief]
}

object RoomStatus {
  implicit val roomStatusFormat = Json.writes[RoomStatus]
}
