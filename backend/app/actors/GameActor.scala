package actors

import akka.actor.{Actor, Props}
import models._
import play.api.libs.json.Json

sealed trait GameMsg

case class PlaceArmy(token: String, territoryId: Int) extends GameMsg

case class MoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int) extends GameMsg

object SerializableGameMsg {
  implicit val placeArmyRead = Json.reads[PlaceArmy]
  implicit val moveArmyRead = Json.reads[MoveArmy]
  implicit val gameMsgRead = Json.reads[GameMsg]
}

object GameActor {
  def props(players: Seq[Player]): Props = Props(new GameActor(players))
}

class GameActor(players: Seq[Player]) extends Actor {
  val logger = play.api.Logger(getClass)
  val game: Game = Game(players) match {
    case Right(createdGame) =>
      logger.info("GOT GAME")
      createdGame
    case Left(errorMsg) =>
      logger.error(s"Error creating game: $errorMsg")
      throw new RuntimeException("Something went wrong lmao")
  }

  logger.debug(s"There are ${game.players.size} players in this game")

  val setupActor = context.actorOf(GameSetupActor.props(players, game))
  val playActor = context.actorOf(GamePlayActor.props(players, game))

  var started: Boolean = false

  override def receive: Receive = {
    case msg: GameMsg =>
      game.state.gamePhase match {
        case Setup =>
          setupActor forward msg
          // Game state changed
          if (game.state.gamePhase == Play) {
            playActor ! StartGamePlay
            started = true
          }
        case Play =>
          if (!started) {
            playActor ! StartGamePlay
            started = true
          }
          playActor forward msg
        case GameOver => ()
      }
  }

}