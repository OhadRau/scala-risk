package actors

import scala.collection.mutable
import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import models._

import scala.collection.mutable.ArrayBuffer

case class ClientWithActor (client: Client, actor: ActorRef)

// Don't forget to add an implicit read for serializableInEvent and write for
// outEvent!

object RootActor {
  def props() = Props(new RootActor())
}

class RootActor() extends Actor {
  implicit val timeout = Timeout(1 second)

  val games: mutable.HashMap[String, ActorRef] = collection.mutable.HashMap[String, ActorRef]()
  val rooms: mutable.HashMap[String, Room] = collection.mutable.HashMap[String, Room]()
  val clients: mutable.HashMap[String, ClientWithActor] = collection.mutable.HashMap[String, ClientWithActor]()

  val chatActor = context.actorOf(ChatActor.props(clients), "chatActor")

  val logger = play.api.Logger(getClass)

  context.system.scheduler.schedule(
    30 seconds, 30 seconds, self, KeepAliveTick()
  )

  override def receive: Receive = {
    case msg: GameMsg =>
      games.get(msg.gameId) match {
        case Some(gameActor) => gameActor forward msg
        case None => clients(msg.token).actor ! Err("No game with that id exists!")
      }

    case msg: ChatMsg =>
      chatActor forward msg

    case RegisterClient(client, actor) =>
      clients += (client.token -> ClientWithActor(client, actor))
      logger.debug(s"Generated token ${client.token} for client!\n")
      actor ! Token(client.token)

    case CreateRoom(roomName: String, token: String) =>
      createRoom(roomName, token)

    case JoinRoom(roomId: String, token: String) =>
      joinRoom(roomId, token)

    case ListRoom(token: String) =>
      sendRoomListing(token)

    case CheckName(token: String, name: String) => checkName(token, name)

    case AssignName(name, token) =>
      if (clients.exists(_._2.client.name.contains(name))) {
        clients(token).actor ! Err("Name is not unique!")
      } else {
        clients(token).client.name = Some(name)
        logger.debug(s"$name assigned to client")
        notifyClientsChanged()
      }

    case ClientReady(roomId, token) =>
      ready(roomId, token)

    case StartGame(roomId, token) =>
      startGame(roomId, token)

    case _: KeepAliveTick =>
      checkAlive()

    case Pong(token) =>
      clients(token).client.alive = true
      logger.debug(s"Client $token Ponged.")
  }

  def checkName(token: String, name: String): Unit = {
    clients.get(token) match {
      case Some(clientActor) =>
        val available = !clients.exists(_._2.client.name.contains(name))
        clientActor.actor ! NameCheckResult(available, name)
      case None =>
        logger.error(s"Client with invalid token $token")
    }
  }

  def sendRoomListing(token: String): Unit = {
    clients.get(token) match {
      case Some(clientActor) => {
        notifyRoomsChanged(clientActor)
        logger.info(s"Client $token requested room listing")
      }
      case None =>
        logger.error(s"Client with invalid token $token")
    }
  }

  def createRoom(roomName: String, token: String): Unit = {
    clients.get(token) match {
      case Some(clientActor) => clientActor.client.name match {
        case Some(_) => rooms.get(roomName) match {
          case Some(_) =>
            logger.error(s"Client with token $token tried to create room with duplicate name $roomName")
            clientActor.actor ! Err("A room with that name already exists")
          case None =>
            val room = new Room(roomName, clientActor)
            rooms += (room.roomId -> room)
            logger.debug(s"Created room with roomId ${room.roomId}")
            room.addClient(clientActor)
            clientActor.actor ! CreatedRoom(room.roomId)
            notifyRoomsChanged()
        }
        case None => logger.error(s"Client with token $token tried to create a room, but had no name")
      }
      case None => logger.error(s"Client with invalid token $token")
    }
  }

  def joinRoom(roomId: String, token: String): Unit = {
    clients.get(token) match {
      case Some(clientActor) =>
        rooms.get(roomId) match {
          case Some(room) =>
            if (room.clients.size < 6) {
              room.addClient(clientActor)
              logger.debug(s"Client ${clientActor.client.name} joined room $roomId")
              clientActor.actor ! JoinedRoom(roomId)
              notifyRoomStatus(room)
              notifyRoomsChanged()
            } else {
              clientActor.actor ! Err(s"Room $roomId is full!")
            }
          case None =>
            logger.error(s"PLayer with token $token tried to join invalid room $roomId")
        }
      case None =>
        logger.error(s"Client with invalid token $token")
    }
  }

  def ready(roomId: String, token: String): Unit = {
    rooms.get(roomId) match {
      case Some(room) =>
        if (room.clients.contains(token)) {
          room.setReady(token)
          notifyRoomStatus(room)
          // TODO: Create start game message
//          if (room.clients.size >= 3 && room.statuses.values.forall(status => status == Ready())) {
//            logger.debug(s"Room $roomId is starting a game with ${room.clients.size} players!")
//            startGame(roomId)
//          }
        }
      case None => clients(token).actor ! Err("Cannot find roomId")
    }
  }

  def startGame(roomId: String, token: String): Unit = {
    rooms.get(roomId) match {
      case Some(room) =>
        if (room.host.client.token == token) {
          if (room.statuses.values.count(status => status == Waiting()) == 0) {
            logger.debug("Starting Game!")
            // Create new game
            val playerSeq = rooms(roomId).clients.values.map(client => new Player(
              client.client.name getOrElse "", client = Some(client))).toSeq
            val gameActor = context.actorOf(GameActor.props(playerSeq), s"game-$roomId")
            games += roomId -> gameActor

            // Remove room
            rooms -= roomId

            notifyRoomsChanged()
          } else {
            clients(token).actor ! Err("Not everyone in the room is ready.")
          }
        } else {
          clients(token).actor ! Err("You are not the host.")
        }
      case None => clients(token).actor ! Err("That room does not exist.")
    }
  }

  def notifyClientsChanged(): Unit = {
    val names = new ArrayBuffer[ClientBrief](clients.size)
    for ((_, client) <- clients) {
      val test = ClientBrief(client.client.name getOrElse "", client.client.publicToken)
      names += test
    }
    for ((_, client) <- clients) {
      client.actor ! NotifyClientsChanged(names)
    }
  }

  def notifyRoomsChanged(client: ClientWithActor = null): Unit = {
    val roomBriefs = ArrayBuffer[RoomBrief]()
    for ((_, room) <- rooms) {
      roomBriefs += room.getBrief
    }
    if (client != null) {
      client.actor ! NotifyRoomsChanged(roomBriefs)
    } else {
      for ((_, client) <- clients) {
        client.actor ! NotifyRoomsChanged(roomBriefs)
      }
    }
  }

  def notifyRoomStatus(room: Room): Unit = {
    val status = room.getStatus
    for ((_, client) <- room.clients) {
      client.actor ! NotifyRoomStatus(status)
    }
  }

  def checkAlive(): Unit = {
    logger.debug("Checking Aliveness")
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
  }

  def pingClients(): Unit = {
    for ((_, client) <- clients) {
      client.actor ! Ping("Ping")
    }
  }
}

