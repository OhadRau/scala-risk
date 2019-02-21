package models

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import scala.util.Random


class Game(val state: GameState) {
  val N_PLAYERS_ARMY: Map[Int, Int] = Map((3, 35), (4, 30), (5, 25), (6, 20))

  def initGame: Unit = {
    for (player <- state.players) {
      player.unitCount = N_PLAYERS_ARMY(state.players.length)
      println(s"${player.name} got assigned ${player.unitCount} armies")
    }
  }
}

class GameState(val gameCode: String = Random.alphanumeric take 4 mkString, val players: Seq[Player] = ArrayBuffer()) {
}

object GameState {
  implicit val stateFormat = Json.writes[GameState]
  def apply(gameCode: String, players: Seq[Player]): GameState = new GameState(gameCode, players)
  def unapply(state: GameState): Option[(String, Seq[Player])] =
    Some((state.gameCode, state.players))
}
