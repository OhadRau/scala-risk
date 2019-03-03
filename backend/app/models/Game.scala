package models

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import play.api.libs.functional.syntax._

import scala.collection.immutable.HashMap
import scala.runtime.ScalaRunTime.stringOf
import scala.util.Random


class Game(val state: GameState) {
  def getPlayerByToken(token: String): Option[Player] = {
    state.players.find(_.client.get.client.publicToken == token)
  }

  def players: Seq[Player] = state.players
}

object Game {
  val logger = play.api.Logger(getClass)
  private val N_PLAYERS_ARMY = HashMap(3 -> 35, 4 -> 30, 5 -> 25, 6 -> 20)

  def apply(players: Seq[Player]): Option[Game] = {
    // Setup Territories
    val map: Map = Map.loadFromFile(DefaultMap).getOrElse({
      logger.error("Couldn't load map!")
      return None
    })

    // Assign turn order
    val shuffled = Random.shuffle(players)
    val state: GameState = GameState(shuffled, map)

    for (player <- state.players) {
      player.unitCount = N_PLAYERS_ARMY(state.players.length)
      logger.debug(s"${player.name} got assigned ${player.unitCount} armies")
    }

    Some(new Game(state))
  }
}

class GameState(val players: Seq[Player] = ArrayBuffer(), val map: Map) {
  val territories: Seq[Territory] = new ArrayBuffer[Territory]()
}

object GameState {
  def apply(players: Seq[Player], map: Map): GameState = new GameState(players, map)

  def unapply(state: GameState): Option[(Seq[Player], Map)] = {
    Some((state.players, state.map))
  }

  /*
  * GameState:
  * {
  *   players: [{}],
  *   map: {
  *     territories: [{},{},{}]
  *   },
  * }
  */
  implicit val gameStateReads: Writes[GameState] = (
    (JsPath \ "players").write[Seq[Player]] and
    (JsPath \ "map").write[Map]
  )(unlift(GameState.unapply))
}
