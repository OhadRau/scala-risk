package models

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

class Game(val state: GameState = new GameState()) {
}

class GameState() {
  val players: Seq[Player] = ArrayBuffer()
}

object GameState {
  implicit val stateWrites: Writes[GameState] = (passed: GameState) => Json.obj(
    "players" -> Json.toJson(passed.players)
  )
}
