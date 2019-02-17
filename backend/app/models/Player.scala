package models

import play.api.libs.json.{JsValue, Json, Writes}
import scala.language.postfixOps

import scala.util.Random

class Player() {
  val token: String = Random.alphanumeric take 16 mkString
  var name: String = ""
  var status: Status = Waiting()
}

object Player {
  def apply(): Player = {
    new Player()
  }

  implicit val stateWrites = new Writes[Player] {
    override def writes(player: Player): JsValue = Json.obj(
      "name" -> player.name
    )
  }
}

trait Status
case class Waiting() extends Status
