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

// Don't forget to add an implicit read for serializableInEvent and write for
// outEvent!
sealed trait OutEvent
case class NotifyClientsChanged(strings: Seq[String]) extends OutEvent
case class NotifyRoomsChanged(rooms: Seq[RoomBrief]) extends OutEvent
case class NotifyRoomStatus(roomStatus: RoomStatus) extends OutEvent
case class NotifyGameState(state: GameState) extends OutEvent
case class Ok(msg: String) extends OutEvent
case class Err(msg: String) extends OutEvent
case class Ping(msg: String) extends OutEvent
case class Kill(msg: String) extends OutEvent

sealed trait InEvent
sealed trait SerializableInEvent extends InEvent
// Client first connected, give client token for identification
case class RegisterClient(client: Client, actor: ActorRef) extends InEvent
// Client first connected, give client token for identification
case class KeepAliveTick() extends InEvent
// Client response to our ping
case class Pong(token: String) extends SerializableInEvent
// Client tries to assign name
case class AssignName(name: String, token: String) extends SerializableInEvent
// Client tries to create room
case class CreateRoom(roomName: String, token: String) extends SerializableInEvent
// Client tries to join room
case class JoinRoom(roomId: String, token: String) extends SerializableInEvent
// Client marks himself ready
case class Ready(roomId: String, token: String) extends SerializableInEvent

object SerializableInEvent {
  implicit val assignNameRead = Json.reads[AssignName]
  implicit val joinRoomRead = Json.reads[JoinRoom]
  implicit val createRoomRead = Json.reads[CreateRoom]
  implicit val readyRead = Json.reads[Ready]
  implicit val pongRead = Json.reads[Pong]
  implicit val serializableInEventRead = Json.reads[SerializableInEvent]
}
object OutEvent {
  implicit val notifyGameStateWrite = Json.writes[NotifyGameState]
  implicit val notifyClientsChangedWrite = Json.writes[NotifyClientsChanged]
  implicit val notifyRoomsChangedWrite = Json.writes[NotifyRoomsChanged]
  implicit val notifyRoomStatusWrite = Json.writes[NotifyRoomStatus]
  implicit val okWrite = Json.writes[Ok]
  implicit val pingWrite = Json.writes[Ping]
  implicit val errWrite = Json.writes[Err]
  implicit val killWrite = Json.writes[Kill]

  implicit val outEventFormat = Json.writes[OutEvent]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[SerializableInEvent, OutEvent]
}

object GameActor {
  def props() = Props(new GameActor())
}

class GameActor() extends Actor {
  implicit val timeout = Timeout(1 second)

  val rooms: mutable.HashMap[String, Room] = collection.mutable.HashMap[String, Room]()
  val clients: mutable.HashMap[String, ClientWithActor] = collection.mutable.HashMap[String, ClientWithActor]()

  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    case RegisterClient(client, actor) =>
      clients += (client.token -> ClientWithActor(client, actor))
      logger.debug(s"Generated token ${client.token} for client!\n")
      actor ! Ok(client.token)
    case CreateRoom(roomName: String, token: String) =>
      clients.get(token) match {
        case Some(clientActor) =>
          if (clientActor.client.name != "") {
            rooms.get(roomName) match {
              case Some(_) =>
                logger.error(s"Client with token $token tried to create room with duplicate name $roomName")
                clientActor.actor ! Err("A room with that name already exists")
              case None =>
                val room = new Room(roomName, clientActor)
                rooms += (room.roomId -> room)
                room.addClient(clientActor)
                notifyRoomsChanged()
            }
          } else {
            logger.error(s"Client with token $token tried to create a room, but had no name")
          }
        case None =>
          logger.error(s"Client with invalid token $token")
      }
    case JoinRoom(roomId: String, token: String) =>
      clients.get(token) match {
        case Some(clientActor) =>
          rooms.get(roomId) match {
            case Some(room) =>
              if (room.clients.size < 6) {
                room.clients += (clientActor.client.token -> clientActor)
                logger.debug(s"Client ${clientActor.client.name} joined room $roomId")
                clientActor.actor ! Ok(roomId)
              } else {
                clientActor.actor ! Err(s"Room $roomId is full!")
              }
            case None =>
              logger.error(s"PLayer with token $token tried to join invalid room $roomId")
          }
        case None =>
          logger.error(s"Client with invalid token $token")
      }
    case AssignName(name, token) =>
      if (clients.exists(_._2.client.name == name)) {
        clients(token).actor ! Err("Name is not unique!")
      } else {
        clients(token).client.name = name
        logger.debug(s"$name assigned to client")
        notifyClientsChanged()
      }
    case Ready(roomId, token) =>
      if (rooms(roomId).clients.contains(token)) {
        clients(token).client.status = Status.Ready
        if (rooms(roomId).clients.size >= 3 && clients.forall(p => p._2.client.status == Status.Ready)) {
          logger.debug(s"Room $roomId is starting a game with ${rooms(roomId).clients.size} players!")
          startGame(roomId)
        }
      }
    case _: KeepAliveTick =>
      logger.debug("Received keepalive")
      // Kill all clients who haven't responded since the last keepalive
      val deadClients = clients.values.filter(p => !p.client.alive)
      // TODO: Handle killed clients?
      for (clientWithActor <- deadClients) {
        logger.debug(s"Killing ${clientWithActor.client.name} for inactivity")
        clientWithActor.actor ! Kill("Killed for inactivity")
      }
      clients.retain((_, clientWithActor) => clientWithActor.client.alive)
      clients.values.foreach(clientWithActor => clientWithActor.client.alive = false)
      if (deadClients.nonEmpty) {
        notifyClientsChanged()
      }
      pingClients()
    case Pong(token) =>
      clients(token).client.alive = true
  }

  def startGame(roomId: String): Unit = {
    val playerSeq = rooms(roomId).clients.values.map(client => new Player(client.client.name, client=Some(client))).toSeq
    val game: Game = new Game(new GameState(players = playerSeq))
    game.initGame
//    //TODO: notify client about changes
  }

  def notifyClientsChanged(): Unit = {
    val names = ArrayBuffer[String]()
    for ((_, client) <- clients) {
      names += client.client.name
    }
    for ((_, client) <- clients) {
      client.actor ! NotifyClientsChanged(names)
    }
  }

  def notifyRoomsChanged(): Unit = {
    val roomBriefs = ArrayBuffer[RoomBrief]()
    for ((_, room) <- rooms) {
      roomBriefs += room.getBrief
    }
    for ((_, client) <- clients) {
      client.actor ! NotifyRoomsChanged(roomBriefs)
    }
  }

  def notifyRoomStatus(room: Room): Unit = {
    val status = room.getStatus
    for ((_, client) <- room.clients) {
      client.actor ! NotifyRoomStatus(status)
    }
  }

  def pingClients(): Unit = {
    for ((_, client) <- clients) {
      client.actor ! Ping("Ping")
    }
  }
}

case class ClientWithActor(client: Client, actor: ActorRef)