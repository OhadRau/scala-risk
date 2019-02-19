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

import scala.collection.mutable.ArrayBuffer

// WARNING: Don't forget to add an implicit formatter if adding a new event!
sealed trait OutEvent
case class NotifyPlayerJoined(strings: Seq[String]) extends OutEvent
case class NotifyRoomStatus(roomStatus: RoomStatus) extends OutEvent
case class NotifyGameState(state: GameState) extends OutEvent
case class Ok(msg: String) extends OutEvent
case class Err(msg: String) extends OutEvent

sealed trait InEvent
sealed trait SerializableInEvent extends InEvent
// Player first connected, give player token for identification
case class RegisterPlayer(player: Player, actor: ActorRef) extends InEvent
// Player tries to assign name
case class AssignName(name: String, token: String) extends SerializableInEvent
// Player tries to join room
case class JoinRoom(roomId: String, token: String) extends SerializableInEvent
// Player marks himself ready
case class Ready(token: String) extends SerializableInEvent

object SerializableInEvent {
  implicit val assignNameFormat = Json.format[AssignName]
  implicit val joinRoomFormat = Json.format[JoinRoom]
  implicit val readyFormat = Json.format[Ready]
  implicit val serializableInEventFormat = Json.format[SerializableInEvent]
}
object OutEvent {
  implicit val notifyGameStateFormat = Json.format[NotifyPlayerJoined]
  implicit val notifyPlayerJoined = Json.format[NotifyGameState]
  implicit val notifyRoomStatus = Json.format[NotifyRoomStatus]
  implicit val okFormat = Json.format[Ok]
  implicit val errFormat = Json.format[Err]

  implicit val outEventFormat = Json.format[OutEvent]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[SerializableInEvent, OutEvent]
}

object GameActor {
  def props() = Props(new GameActor())
}

class GameActor() extends Actor {
  implicit val timeout = Timeout(1 second)

  private val game = new Game()
  val rooms: mutable.HashMap[String, Room] = collection.mutable.HashMap[String, Room]()
  val players: mutable.HashMap[String, PlayerWithActor] = collection.mutable.HashMap[String, PlayerWithActor]()

  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    case RegisterPlayer(player, actor) => {
      players += (player.token -> PlayerWithActor(player, actor))
      logger.debug(s"Generated token ${player.token} for player!")
      actor ! Ok(player.token)
    }
    case JoinRoom(roomId: String, token: String) => {
      players.get(token) match {
        case Some(playerActor) =>
          rooms.get(roomId) match {
            case Some(room) =>
              if (room.players.size < 6) {
                room.players += (playerActor.player.token -> playerActor)
                logger.debug(s"Player ${playerActor.player.name} joined room $roomId")
                playerActor.actor ! Ok(roomId)
              } else {
                playerActor.actor ! Err(s"Room $roomId is full!")
              }
            case None =>
              logger.error(s"PLayer with token $token tried to join invalid room $roomId")
          }
        case None =>
          logger.error(s"Player with invalid token $token")
      }
    }
    case AssignName(name, token) =>
      if (game.state.players.exists(p => p.name == name)) {
        players(token).actor ! Err("Name is not unique!")
      } else {
        players(token).player.name = name
        logger.debug(s"$name assigned to player")
        notifyPlayerJoined()
      }
    case Ready(token) => {
      players(token).player.status = Status.Ready
      if (players.size >= 3 && players.forall(p => p._2.player.status == Ready)) {
        logger.debug("All players ready! Starting game!")
        // TODO: Actually start the game
      }
    }
  }

  def notifyPlayerJoined(): Unit = {
    val names = ArrayBuffer[String]()
    for ((_, player) <- players) {
      names += player.player.name
    }
    for ((_, player) <- players) {
      player.actor ! NotifyPlayerJoined(names)
    }
  }

  def notifyRoomStatus(room: Room): Unit = {
    val status = room.getStatus
    for ((_, player) <- room.players) {
      player.actor ! NotifyRoomStatus(status)
    }
  }
}

case class PlayerWithActor(player: Player, actor: ActorRef)