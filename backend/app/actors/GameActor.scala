package actors

import scala.collection.mutable
import scala.language.postfixOps
import scala.concurrent.duration._
import akka.pattern.ask
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import models._
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer

// WARNING: Don't forget to add an implicit formatter if adding a new event!
sealed trait OutEvent
case class NotifyGameState(state: GameState) extends OutEvent
case class Ok(msg: String) extends OutEvent
case class Err(msg: String) extends OutEvent

sealed trait InEvent
case class PlayerJoined(name: String, token: String) extends InEvent
case class GenerateToken(gameCode: Int) extends InEvent
case class Ready(token: String) extends InEvent

object Implicits {
  implicit val playerJoinedFormat = Json.format[PlayerJoined]
  implicit val generateTokenFormat = Json.format[GenerateToken]
  implicit val readyFormat = Json.format[Ready]

  implicit val notifyGameStateFormat = Json.format[NotifyGameState]
  implicit val okFormat = Json.format[Ok]
  implicit val errFormat = Json.format[Err]

  implicit val inEventFormat = Json.format[InEvent]
  implicit val outEventFormat = Json.format[OutEvent]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[InEvent, OutEvent]
}

object GameActor {
  def props(out: ActorRef) = Props(new GameActor(out))
}

class GameActor(out: ActorRef) extends Actor {
  implicit val timeout = Timeout(1 second)

  private val game = new Game()
  private val players: mutable.HashMap[String, PlayerWithActor] = collection.mutable.HashMap[String, PlayerWithActor]()

  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    case GenerateToken(gameCode, _) => {
      // TODO: Use the gameCode for something
      if (players.size < 6) {
        val player = new Player()
        players += (player.token -> PlayerWithActor(player, sender))
        logger.debug("Generated token for player")
        sender ! Ok(player.token)
      } else {
        sender ! Err("Game is full!")
      }
    }
    case PlayerJoined(name, token, _) => {
      if (game.state.players.exists(p => p.name == name)) {
        sender ! Err("Name is not unique!")
      } else {
        players(token).player.name = name
        logger.debug(s"$name Joined")
        sender ! Ok(name)
        notifyGameState()
      }
    }
    case Ready(token, _) => {
      players(token).player.status = Status.Ready
      if (players.size >= 3 && players.forall(p => p._2.player.status == Ready)) {
        logger.debug("All players ready! Starting game!")
      }
    }
  }

  def notifyGameState(): Unit = {
    for ((_, player) <- players) {
      player.actor ! NotifyGameState(game.state)
    }
  }
}

case class PlayerWithActor(player: Player, actor: ActorRef)