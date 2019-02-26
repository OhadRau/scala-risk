package models

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import scala.runtime.ScalaRunTime.stringOf
import scala.util.Random


class Game(val state: GameState) {
  val N_PLAYERS_ARMY: Map[Int, Int] = Map(3 -> 35, 4 -> 30, 5 -> 25, 6 -> 20)
  val logger = play.api.Logger(getClass)
  var turnOrder: Seq[Player] = players;

  def initGame(): Unit = {
    turnOrder = assignTurnOrder(state.players)

    for (player <- state.players) {
      player.unitCount = N_PLAYERS_ARMY(state.players.length)
      logger.debug(s"${player.name} got assigned ${player.unitCount} armies")
    }
  }

  def assignTurnOrder(array: Seq[Player]): Seq[Player] = {
    val turnOrder = Random.shuffle(array)
    logger.debug(stringOf(turnOrder))
    turnOrder
  }

  def players: Seq[Player] = state.players
}

object Game {
  def apply(players: Seq[Player]): Game = new Game(GameState(players))
}

class GameState(val players: Seq[Player] = ArrayBuffer()) {
}

object GameState {
  implicit val stateFormat = Json.writes[GameState]
  def apply(players: Seq[Player]): GameState = new GameState(players)
  def unapply(state: GameState): Option[Seq[Player]] = Some(state.players)
}
