package models

import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import play.api.libs.functional.syntax._

import scala.collection.immutable.HashMap
import scala.runtime.ScalaRunTime.stringOf
import scala.util.Random

trait GamePhase

case object Setup extends GamePhase
case object Play extends GamePhase
case object GameOver extends GamePhase

object SerializableGamePhase {
  implicit object gamePhaseWrites extends Writes[GamePhase] {
    def writes(phase: GamePhase): JsValue = phase match {
      case Setup => Json.toJson("Setup")
      case Play => Json.toJson("Play")
      case GameOver => Json.toJson("GameOver")
    }
  }
}

class Game(val state: GameState, val armyAllotmentSize: Int) {
  def getPlayerByToken(token: String): Option[Player] = {
    state.players.find(_.client.get.client.publicToken == token)
  }

  def players: Seq[Player] = state.players
}

object Game {
  val logger = play.api.Logger(getClass)
  private val getArmyAllotmentSize = HashMap(3 -> 3, 4 -> 30, 5 -> 25, 6 -> 20)

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

      new Game(state, armyAllotmentSize)
    }
}

case class GameState(val players: Seq[Player] = ArrayBuffer(), val map: Map, var gamePhase: GamePhase = Setup)

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
  implicit val gamePhaseWrites: Writes[GamePhase] =
    SerializableGamePhase.gamePhaseWrites
  implicit val gameStateWrites: Writes[GameState] = (
    (JsPath \ "players").write[Seq[Player]] and
    (JsPath \ "map").write[Map] and
    (JsPath \ "gamePhase").write[GamePhase]
  )(unlift(GameState.unapply))
}
