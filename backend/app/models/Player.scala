package models

import play.api.libs.json._

import scala.language.postfixOps
import scala.util.Random

object Status extends Enumeration {
  type Status = Value
  val Waiting = Value("Waiting")
}

import Status._

case class Player(var token: String = Random.alphanumeric take 16 mkString,
                  var name: String = "",
                  var status: Status = Waiting)

object Player {
  def apply(): Player = {
    new Player()
  }

  implicit val statusFormat = Json.formatEnum(Status)
  implicit val stateFormat = Json.format[Player]
}
