package models

import actors.PlayerWithActor
import play.api.libs.json.{Json, OFormat}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import scala.util.Random

class Room(roomName: String, var host: PlayerWithActor) {
  val players: mutable.HashMap[String, PlayerWithActor] = collection.mutable.HashMap[String, PlayerWithActor]()
  val roomId: String = Random.alphanumeric take 16 mkString

  def getStatus: RoomStatus = {
    val names = ArrayBuffer[String]()
    for ((_, player) <- players) {
      names += player.player.name
    }
    RoomStatus(names)
  }

  def getBrief: RoomBrief = {
    RoomBrief(roomName, host.player.name, roomId, players.size)
  }

  def addPlayer(player: PlayerWithActor): Unit = {
    players += (player.player.token -> player)
  }
}

case class RoomStatus(players: Seq[String])
case class RoomBrief(name: String, hostName: String, roomId: String, playerCount: Int)

object RoomStatus {
  implicit val roomStatusFormat: OFormat[RoomStatus] = Json.format[RoomStatus]
}

object RoomBrief {
  implicit val roomBriefFormat: OFormat[RoomBrief] = Json.format[RoomBrief]
}
