package actors

import scala.collection.mutable
import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import models._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

case class ClientWithActor(client: Client, var actor: ActorRef)

// Don't forget to add an implicit read for serializableInEvent and write for
// outEvent!

object RootActor {
  def props(): Props = Props(new RootActor())
}

class RootActor() extends Actor {
  implicit val timeout = Timeout(1 second)

  val games: mutable.HashMap[String, ActorRef] = mutable.HashMap[String, ActorRef]()
  val rooms: mutable.HashMap[String, Room] = mutable.HashMap[String, Room]()
  val clients: mutable.HashMap[String, ClientWithActor] = mutable.HashMap[String, ClientWithActor]()
  val publicToPrivate: mutable.HashMap[String, String] = mutable.HashMap[String, String]()

  val chatActor = context.actorOf(ChatActor.props(clients, publicToPrivate, rooms), "chatActor")

  val logger = play.api.Logger(getClass)

  context.system.scheduler.schedule(
    30 seconds, 30 seconds, self, KeepAliveTick()
  )

  // TODO: Refactor error checking somewhere
  override def receive: Receive = {
    case msg: AuthenticatedMsg =>
      handleAuthenticatedMessage(msg)
    case msg: RootMsg =>
      handleUnauthenticatedMessage(msg)
  }

  def handleAuthenticatedMessage(msg: AuthenticatedMsg): Unit = {
    clients.get(msg.token) match {
      case Some(matchedClient) =>
        implicit val client: ClientWithActor = matchedClient
        msg match {
          case ForwardToGame(_, gameId, gameMsg) =>
            games.get(gameId) match {
              case Some(gameActor) => gameActor forward gameMsg
              case None => client.actor ! Err("No game with that id exists!")
              case _ =>
            }
          case ForwardToChat(_, chatMsg) => chatActor forward(client, chatMsg)
          case a: AuthenticatedRootMsg => handleAuthenticatedRootMessage(a)
          case r: RoomMsg => handleRoomMsg(r)
          case _ =>
            logger.info("Lol matched none")
        }
      case None =>
        sender ! Err("Invalid Token")
        logger.debug("Client with invalid Token")
    }
  }

  def handleUnauthenticatedMessage(msg: RootMsg): Unit = msg match {
    case RegisterClient(client, actor) => {
      clients += (client.token -> ClientWithActor(client, actor))
      publicToPrivate += (client.publicToken -> client.token)
      logger.debug(s"Generated token ${client.token} for client!\n")
      actor ! Token(client.token, client.publicToken)
    }

    case KeepAliveTick() => checkAlive()
    case other => logger.debug(other.toString)
  }

  def handleAuthenticatedRootMessage(authenticatedRootMsg: AuthenticatedRootMsg)(implicit client: ClientWithActor)
  : Unit = {
    authenticatedRootMsg match {
      case CreateRoom(roomName: String, token: String) => createRoom(roomName, token)
      case ListRoom(token: String) => sendRoomListing(token)
      case CheckName(token: String, name: String) => checkName(token, name)
      case AssignName(name, token) => assignName(token, name)
      case SetToken(token, oldToken) =>
        clients.get(oldToken) match {
          case Some(oldClientWithActor) =>
            val currentSocketActor = client.actor
            clients -= token
            logger.debug("New Client " + currentSocketActor.toString())
            oldClientWithActor.actor = currentSocketActor
            currentSocketActor ! Token(oldToken, clients(oldToken).client.publicToken)
            notifyClientResumeStatus(oldClientWithActor)
          case None => clients.retain((_, clientWithActor) => clientWithActor.actor != sender)
        }
      case Pong(token) =>
        clients(token).client.alive = true
    }
  }

  def notifyClientResumeStatus(client: ClientWithActor): Unit = {
    val room = rooms.find(_._2.clients.contains(client.client.token)) match {
      case Some(value) => Some(value._2.roomId)
      case None => None
    }
    client.actor ! NotifyClientResumeStatus(
      client.client.name.getOrElse(""),
      room.getOrElse(""),
      client.client.game.getOrElse("")
    )
    notifyRoomsChanged(Some(client))
    notifyClientsChanged()
    room match {
      case Some(r) =>
        notifyRoomStatus(rooms(r), Some(client))
        if (rooms(r).playing) {
          games(r) ! GameRequestInfo(client.client.token)
        }
      case None =>
    }
  }

  def handleRoomMsg(msg: RoomMsg)(implicit clientWithActor: ClientWithActor): Unit = {
    rooms.get(msg.roomId) match {
      case Some(matchedRoom) =>
        implicit val room: Room = matchedRoom
        msg match {
          case JoinRoom(roomId, token) => joinRoom(roomId, token)
          case StartGame(roomId, token) => startGame(roomId, token)
          case ClientReady(roomId, token, status) => ready(roomId, token, status)
          case LeaveRoom(roomId, token) => leaveRoom(roomId, token)
          case PlayAgain(roomId, token) => playAgain(roomId, token)
        }
      case _ =>
        logger.error(s"PLayer with token ${msg.token} tried to join invalid room ${msg.roomId}")
        sender() ! Err("No such room")
    }
  }

  def assignName(token: String, name: String)(implicit client: ClientWithActor): Unit = {
    if (client.client.name.isDefined && client.client.name.getOrElse("") == name) {
      client.actor ! NameAssignResult(success = false, name, "Name already assigned!")
    } else if (clients.values.exists(_.client.name.getOrElse("") == name)) {
      client.actor ! NameAssignResult(success = false, name, "Name is not unique!")
    } else {
      client.client.name = Some(name)
      client.actor ! NameAssignResult(success = true, name)
      logger.debug(s"$name assigned to client")
      notifyClientsChanged()
    }
  }

  def checkName(token: String, name: String): Unit = {
    val available = clients.values forall (client => client.client.name.getOrElse("") != name)
    clients(token).actor ! NameCheckResult(available, name)
  }

  def sendRoomListing(token: String): Unit = {
    notifyRoomsChanged(Some(clients(token)))
    logger.info(s"Client $token requested room listing")
  }

  def createRoom(roomName: String, token: String)(implicit clientActor: ClientWithActor): Unit = {
    clientActor.client.name match {
      case Some(_) => rooms.get(roomName) match {
        case Some(_) =>
          logger.error(s"Client with token $token tried to create room with duplicate name $roomName")
          clientActor.actor ! RoomCreationResult(success = false, "A room with that name already exists")
        case None =>
          val room = new Room(roomName, clientActor)
          rooms += (room.roomId -> room)
          logger.debug(s"Created room with roomId ${room.roomId}")
          clientActor.actor ! CreatedRoom(room.roomId)
          room.addClient(clientActor)
          notifyRoomsChanged()
          notifyRoomStatus(room, Some(clientActor))
      }
      case None =>
        logger.error(s"Client with token $token tried to create a room, but had no name")
        clientActor.actor ! RoomCreationResult(success = false, s"Client with token $token tried to create a room, " +
          s"but had no name")
    }
  }

  def joinRoom(roomId: String, token: String)(implicit clientActor: ClientWithActor, room: Room): Unit = {
    if (room.clients.size < 6) {
      room.addClient(clientActor)
      logger.debug(s"Client ${clientActor.client.name} joined room $roomId")
      notifyRoomStatus(room)
      notifyRoomsChanged()
    } else {
      clientActor.actor ! Err(s"Room $roomId is full!")
    }
  }

  def leaveRoom(roomId: String, token: String)(implicit clientActor: ClientWithActor, room: Room): Unit = {
    room.removeClient(token)
    // If player who left was the host, assign a random player to be the host
    if (room.clients.isEmpty) {
      rooms -= room.roomId
    } else if (room.host.client.token == clientActor.client.token) {
      val random = Random
      val newHost = room.clients.keySet.iterator.drop(random.nextInt(room.clients.size)).next
      room.host = room.clients(newHost)
    }
    notifyRoomStatus(room)
    notifyRoomsChanged()
  }

  def playAgain(roomId: String, token: String)(implicit room: Room): Unit = {
    if (room.playing) {
      // Delete game, reset room status, and set all other players to unready
      context.stop(games(roomId))
      games.remove(roomId)
      room.playing = false
      for ((token, _) <- room.clients) {
        room.setReady(token, false)
        clients(token).client.game = None
      }
    }
    room.setReady(token, true)
    notifyRoomStatus(room)
  }

  def ready(roomId: String, token: String, status: Boolean)(implicit room: Room): Unit = {
    if (room.clients.contains(token)) {
      room.setReady(token, status)
      notifyRoomStatus(room)
    }
  }

  def startGame(roomId: String, token: String)(implicit clientWithActor: ClientWithActor, room: Room): Unit = {
    logger.debug("received startGame")
    logger.debug(s"host: ${room.host.client.token}, token: ${token}")
    if (room.host.client.token == token) {
      if (room.statuses.filter(_._1 != token).values.count(status => status == Waiting()) == 0) {
        logger.debug("Starting Game!")
        // Create new game
        val playerSeq = rooms(roomId).clients.values.map(client => new Player(
          client.client.name getOrElse "", client = Some(client))).toSeq
        val gameActor = context.actorOf(GameActor.props(playerSeq), s"game-$roomId")
        games += roomId -> gameActor
        room.playing = true
        notifyRoomsChanged()
      } else {
        logger.debug("Sending error message!")
        clientWithActor.actor ! Err("Not everyone in the room is ready.")
      }
    } else {
      clientWithActor.actor ! Err("You are not the host.")
    }
  }

  def notifyClientsChanged(): Unit = {
    val names = clients.values.filter(_.client.name.isDefined).map(client =>
      ClientBrief(client.client.name.getOrElse(""), client.client.publicToken)
    ).toSeq
    clients.values foreach (_.actor ! NotifyClientsChanged(names))
  }

  def notifyRoomsChanged(client: Option[ClientWithActor] = None): Unit = {
    val roomBriefs = rooms.values.map(_.getBrief).toSeq
    client match {
      case Some(receiver) => receiver.actor ! NotifyRoomsChanged(roomBriefs)
      case None => clients.values foreach (_.actor ! NotifyRoomsChanged(roomBriefs))
    }
  }

  def notifyRoomStatus(room: Room, actor: Option[ClientWithActor] = Option.empty): Unit = {
    val status = room.getStatus
    actor match {
      case Some(value) =>
        value.actor ! NotifyRoomStatus(status)
      case None =>
        room.clients.values foreach (_.actor ! NotifyRoomStatus(status))
    }
  }

  def checkAlive(): Unit = {
    // Kill all clients who haven't responded since the last keepalive
    val deadClients = clients.values.filter(p => !p.client.alive)
    for (clientWithActor <- deadClients) {
      logger.debug(s"Killing ${clientWithActor.client.name} for inactivity")
      clientWithActor.actor ! Kill("Killed for inactivity")
      context.stop(clientWithActor.actor);
    }
    rooms.retain((_, room) => {
      room.clients.retain((_, clientWithActor) => clientWithActor.client.alive)
      room.clients.nonEmpty
    })
    clients.retain((_, clientWithActor) => clientWithActor.client.alive)
    clients.values.foreach(clientWithActor => clientWithActor.client.alive = false)
    if (deadClients.nonEmpty) {
      notifyClientsChanged()
    }
    pingClients()
  }

  def pingClients(): Unit = {
    for ((_, client) <- clients) {
      client.actor ! Ping("Ping")
    }
  }
}

