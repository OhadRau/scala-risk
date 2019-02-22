package models

import play.api.libs.json._

import scala.language.postfixOps
import scala.util.Random

case class Client(token: String = Random.alphanumeric take 16 mkString,
                  var name: Option[String] = None, var alive: Boolean = true)

object Client {
  implicit val stateFormat = Json.format[Client]
}
