package models

import play.api.libs.json._


case class Client(var unitCount: Int = 0) {

}

object Client {
  implicit val clientFormat = Json.format[Client]
}