package actors

import java.time.LocalDateTime

import akka.actor.{Actor, Props}
import models.Room
import play.api.libs.json.Json

import scala.collection.mutable.HashMap

sealed trait ChatMsg
case class MessageToUser(recipientPublic: String, message: String) extends ChatMsg
case class MessageToRoom(roomId: String, message: String) extends ChatMsg

object SerializableChatMsg {
  implicit val messageToUserRead = Json.reads[MessageToUser]
  implicit val messageToRoomRead = Json.reads[MessageToRoom]
  implicit val chatMsgRead = Json.reads[ChatMsg]
}

object ChatActor {
  def props(clients: HashMap[String, ClientWithActor], rooms: HashMap[String, Room]): Props =
    Props(new ChatActor(clients, rooms))
}

class ChatActor(clients: HashMap[String, ClientWithActor], val rooms: HashMap[String, Room]) extends Actor {
  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    // TODO: Not O(n) search
    case (player: ClientWithActor, MessageToUser(publicToken, message)) =>
      handleMessageToUser(player, publicToken, message)
    case (player: ClientWithActor, MessageToRoom(roomId, message)) =>
      handleMessageToRoom(player, rooms(roomId), message)
  }

  def handleMessageToUser(player: ClientWithActor, publicToken: String, message: String): Unit = {
    for {
      playerName <- player.client.name
    } yield {
      val playerToken = player.client.publicToken
      val playerActor = player.actor
      val recipient = clients(publicToken)
      val time = LocalDateTime.now
      recipient.actor ! UserMessage(playerName, playerToken, message, time.toString)
      playerActor ! UserMessage(playerName, playerToken, message, time.toString)
    }
  }

  def handleMessageToRoom(player: ClientWithActor, room: Room, message: String): Unit = {
    for {
      playerName <- player.client.name
    } yield {
      val time = LocalDateTime.now
      room.clients.values foreach (client => {
        client.actor ! RoomMessage(playerName, message, time.toString)
      })
    }
  }
}
