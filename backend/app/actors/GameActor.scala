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

sealed trait OutEvent
case class RequestName(token: String) extends OutEvent
case class NotifyGameState(state: GameState) extends OutEvent
case class Ok(msg: String) extends OutEvent

sealed trait InEvent
case class PlayerJoined(name: String, token: String) extends InEvent
case class GenerateToken(player: Player) extends InEvent

object Implicits {
  implicit val playerJoinedFormat = Json.format[PlayerJoined]
  implicit val generateTokenFormat = Json.format[GenerateToken]

  implicit val requestNameFormat = Json.format[RequestName]
  implicit val notifyGameStateFormat = Json.format[NotifyGameState]
  implicit val okFormat = Json.format[Ok]

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
    case GenerateToken(player) => {
      players += (player.token -> PlayerWithActor(player, sender))
      logger.debug("Generated token for player")
      requestName(player.token, sender)
    }
    case PlayerJoined(name, token) => {
      if (game.state.players.exists(p => p.name == name)) {
        requestName(token, sender)
      } else {
        players(token).player.name = name
        logger.debug(s"$name Joined")
        sender ! Ok(name)
        notifyGameState()
      }
    }
  }

  def notifyGameState(): Unit = {
    for ((_, player) <- players) {
      player.actor ! NotifyGameState(game.state)
    }
  }

  def requestName(token: String, actor: ActorRef): Unit = {
    actor ! RequestName(token)
  }
}

case class PlayerWithActor(player: Player, actor: ActorRef)