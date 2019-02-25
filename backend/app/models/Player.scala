package models

import actors.ClientWithActor
import play.api.libs.json._

import scala.language.postfixOps
import scala.util.Random

class Player(val name: String, var unitCount: Int = 0, val client: Option[ClientWithActor] = None) {

}

object Player {
  implicit val playerFormat = Json.writes[Player]

  def apply(name: String, unitCount: Int): Player = new Player(name, unitCount)

  def unapply(player: Player): Option[(String, Int)] =
    Some((player.name, player.unitCount))
}

