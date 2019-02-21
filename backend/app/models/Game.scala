package models

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


class Game(val state: GameState = new GameState()) {
  val N_PLAYERS_ARMY: Map[Int, Int] = Map((3, 35), (4, 30), (5, 25), (6, 20))

  def initGame: Unit = {
    for (player <- state.players) {
      player.client.unitCount = N_PLAYERS_ARMY(state.players.length)
      println(s"${player.name} got assigned ${player.client.unitCount} armies")
    }
  }
}

case class GameState(gameCode: String = Random.nextInt(10000).toString,
                     players: Seq[Player] = ArrayBuffer())

object GameState {
  implicit val stateFormat = Json.format[GameState]
}
