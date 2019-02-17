package models

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Game(val state: GameState = new GameState()) {
}

case class GameState(gameCode: String = Random.nextInt(10000).toString,
                     players: Seq[Player] = ArrayBuffer())

object GameState {
  implicit val stateFormat = Json.format[GameState]
}
