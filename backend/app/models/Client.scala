package models

import play.api.libs.json._

import scala.language.postfixOps
import scala.util.Random

case class Client(token: String = Random.alphanumeric take 16 mkString, publicToken: String = Random.alphanumeric take 16 mkString,
                  var name: Option[String] = None, var game: Option[String] = None, var alive: Boolean = true)

case class ClientBrief(name: String, publicToken: String)

object Client {
  implicit val clientFormat = Json.format[Client]
}
object ClientBrief {
  implicit val clientBriefFormat = Json.format[ClientBrief]
}
