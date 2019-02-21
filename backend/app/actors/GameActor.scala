package actors

import scala.collection.mutable
import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import models._
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer

import scala.collection.mutable.ArrayBuffer

// WARNING: Don't forget to add an implicit formatter if adding a new event!
sealed trait OutEvent
case class NotifyPlayersChanged(strings: Seq[String]) extends OutEvent
case class NotifyRoomsChanged(rooms: Seq[RoomBrief]) extends OutEvent
case class NotifyRoomStatus(roomStatus: RoomStatus) extends OutEvent
case class NotifyGameState(state: GameState) extends OutEvent
case class Ok(msg: String) extends OutEvent
case class Err(msg: String) extends OutEvent
case class Ping(msg: String) extends OutEvent
case class Kill(msg: String) extends OutEvent

sealed trait InEvent
sealed trait SerializableInEvent extends InEvent
// Player first connected, give player token for identification
case class RegisterPlayer(player: Player, actor: ActorRef) extends InEvent
// Player first connected, give player token for identification
case class KeepAliveTick() extends InEvent
// Client response to our ping
case class Pong(token: String) extends SerializableInEvent
// Player tries to assign name
case class AssignName(name: String, token: String) extends SerializableInEvent
// Player tries to create room
case class CreateRoom(roomName: String, token: String) extends SerializableInEvent
// Player tries to join room
case class JoinRoom(roomId: String, token: String) extends SerializableInEvent
// Player marks himself ready
case class Ready(token: String) extends SerializableInEvent

object SerializableInEvent {
  implicit val assignNameFormat = Json.format[AssignName]
  implicit val joinRoomFormat = Json.format[JoinRoom]
  implicit val createRoomFormat = Json.format[CreateRoom]
  implicit val readyFormat = Json.format[Ready]
  implicit val pongFormat = Json.format[Pong]
  implicit val serializableInEventFormat = Json.format[SerializableInEvent]
}
object OutEvent {
  implicit val notifyGameStateFormat = Json.format[NotifyGameState]
  implicit val notifyPlayersChanged = Json.format[NotifyPlayersChanged]
  implicit val notifyRoomsChanged = Json.format[NotifyRoomsChanged]
  implicit val notifyRoomStatus = Json.format[NotifyRoomStatus]
  implicit val okFormat = Json.format[Ok]
  implicit val pingFormat = Json.format[Ping]
  implicit val errFormat = Json.format[Err]
  implicit val killFormat = Json.format[Kill]

  implicit val outEventFormat = Json.format[OutEvent]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[SerializableInEvent, OutEvent]
}

object GameActor {
  def props() = Props(new GameActor())
}

class GameActor() extends Actor {
  implicit val timeout = Timeout(1 second)

  val rooms: mutable.HashMap[String, Room] = collection.mutable.HashMap[String, Room]()
  val players: mutable.HashMap[String, PlayerWithActor] = collection.mutable.HashMap[String, PlayerWithActor]()

  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    case RegisterPlayer(player, actor) =>
      players += (player.token -> PlayerWithActor(player, actor))
      logger.debug(s"Generated token ${player.token} for player!\n")
      actor ! Ok(player.token)
    case CreateRoom(roomName: String, token: String) =>
      players.get(token) match {
        case Some(playerActor) =>
          if (playerActor.player.name != "") {
            rooms.get(roomName) match {
              case Some(_) =>
                logger.error(s"Player with token $token tried to create room with duplicate name $roomName")
                playerActor.actor ! Err("A room with that name already exists")
              case None =>
                val room = new Room(roomName, playerActor)
                rooms += (room.roomId -> room)
                room.addPlayer(playerActor)
                notifyRoomsChanged()
            }
          } else {
            logger.error(s"Player with token $token tried to create a room, but had no name")
          }
        case None =>
          logger.error(s"Player with invalid token $token")
      }
    case JoinRoom(roomId: String, token: String) =>
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
    case AssignName(name, token) =>
      if (players.exists(_._2.player.name == name)) {
        players(token).actor ! Err("Name is not unique!")
      } else {
        players(token).player.name = name
        logger.debug(s"$name assigned to player")
        notifyPlayersChanged()
      }
    case Ready(token) =>
      players(token).player.status = Status.Ready
      if (players.size >= 3 && players.forall(p => p._2.player.status == Status.Ready)) {
        logger.debug("All players ready! Starting game!")
        startGame()
      }
    case _: KeepAliveTick =>
      logger.debug("Received keepalive")
      // Kill all players who haven't responded since the last keepalive
      val deadClients = players.values.filter(p => !p.player.alive)
      // TODO: Handle killed clients?
      for (playerWithActor <- deadClients) {
        logger.debug(s"Killing ${playerWithActor.player.name} for inactivity")
        playerWithActor.actor ! Kill("Killed for inactivity")
      }
      players.retain((_, playerWithActor) => playerWithActor.player.alive)
      players.values.foreach(playerWithActor => playerWithActor.player.alive = false)
      if (deadClients.nonEmpty) {
        notifyPlayersChanged()
      }
      pingClients()
    case Pong(token) =>
      players(token).player.alive = true
  }

  def startGame(): Unit = {
    val (_, vals) = players.toSeq.unzip
    var PlayerSeq = ArrayBuffer[Player]()
    for (value <- vals) PlayerSeq += value.player
    val game: Game = new Game(GameState(players = PlayerSeq))
    game.initGame
    //TODO: notify client about changes
  }

  def notifyPlayersChanged(): Unit = {
    val names = ArrayBuffer[String]()
    for ((_, player) <- players) {
      names += player.player.name
    }
    for ((_, player) <- players) {
      player.actor ! NotifyPlayersChanged(names)
    }
  }

  def notifyRoomsChanged(): Unit = {
    val roomBriefs = ArrayBuffer[RoomBrief]()
    for ((_, room) <- rooms) {
      roomBriefs += room.getBrief
    }
    for ((_, player) <- players) {
      player.actor ! NotifyRoomsChanged(roomBriefs)
    }
  }

  def notifyRoomStatus(room: Room): Unit = {
    val status = room.getStatus
    for ((_, player) <- room.players) {
      player.actor ! NotifyRoomStatus(status)
    }
  }

  def pingClients(): Unit = {
    for ((_, player) <- players) {
      player.actor ! Ping("Ping")
    }
  }
}

case class PlayerWithActor(player: Player, actor: ActorRef)