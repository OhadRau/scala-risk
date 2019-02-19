package models

import actors.PlayerWithActor
import play.api.libs.json.{Json, OFormat}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Room() {
  val players: mutable.HashMap[String, PlayerWithActor] = collection.mutable.HashMap[String, PlayerWithActor]()

  def getStatus: RoomStatus = {
    val names = ArrayBuffer[String]()
    for ((_, player) <- players) {
      names += player.player.name
    }
    RoomStatus(names)
  }
}

case class RoomStatus(players: Seq[String])

object RoomStatus {
  implicit val roomStatusFormat: OFormat[RoomStatus] = Json.format[RoomStatus]
}
