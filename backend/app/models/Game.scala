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
  private val getArmyAllotmentSize = HashMap(3 -> 35, 4 -> 30, 5 -> 25, 6 -> 20)

  def apply(players: Seq[Player]): Either[String, Game] =
    for {
      map <- Map.loadFromFile("default")
      () = logger.info("Got Map!")
    } yield {
      // Assign turn order
      val shuffled = Random.shuffle(players)
      logger.info("Shuffled Players!")
      val state: GameState = GameState(shuffled, map)

      val armyAllotmentSize = getArmyAllotmentSize(state.players.length)

      state.players foreach (player => {
        player.unitCount = armyAllotmentSize
        logger.debug(s"${player.name} got assigned ${player.unitCount} armies")
      })

      new Game(state)
    }
}

case class GameState(val players: Seq[Player] = ArrayBuffer(), val map: Map)

object GameState {
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
