package actors

import akka.actor.ActorRef
import models._
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer

sealed trait AuthenticatedMsg { val token: String }

// Messages for which actor
sealed trait RootMsg
sealed trait AuthenticatedRootMsg extends RootMsg with AuthenticatedMsg

sealed trait RoomMsg extends AuthenticatedMsg { val roomId: String }

sealed trait ChatMsg extends AuthenticatedMsg
case class MessageToUser(token: String, recipientPublic: String, message: String) extends SerializableInEvent with ChatMsg
case class MessageToRoom(token: String, roomId: String, message: String) extends SerializableInEvent with ChatMsg
case class UserMessage(senderName: String, publicToken: String, message: String, timestamp: String) extends OutEvent
case class RoomMessage(senderName: String, message: String, timestamp: String) extends OutEvent

sealed trait GameMsg extends AuthenticatedMsg { val gameId: String }

// Messages that are sent to the client
sealed trait OutEvent

case class NotifyClientsChanged(strings: Seq[ClientBrief]) extends OutEvent

case class NotifyRoomsChanged(rooms: Seq[RoomBrief]) extends OutEvent

case class NotifyRoomStatus(roomStatus: RoomStatus) extends OutEvent

case class Token(token: String, publicToken: String) extends OutEvent

case class CreatedRoom(token: String) extends OutEvent

case class JoinedRoom(token: String) extends OutEvent

case class NameCheckResult(available: Boolean, name: String) extends OutEvent

case class Ok(msg: String) extends OutEvent

case class Err(msg: String) extends OutEvent

case class Ping(msg: String) extends OutEvent

case class Kill(msg: String) extends OutEvent


case class NotifyGameStarted(state: GameState) extends OutEvent

case class NotifyGameState(state: GameState) extends OutEvent


// Messages which are read (including sent from ourself to ourself
sealed trait InEvent

// Messages which are sent from the client, and can be deserialized
sealed trait SerializableInEvent extends InEvent

// Client first connected, give client token for identification
case class RegisterClient(client: Client, actor: ActorRef) extends InEvent with RootMsg

// Client first connected, give client token for identification
case class KeepAliveTick() extends InEvent with RootMsg

// Client request to list rooms
case class ListRoom(token: String) extends SerializableInEvent with AuthenticatedRootMsg

// Client request to validate a name's availability
case class CheckName(token: String, name: String) extends SerializableInEvent with AuthenticatedRootMsg

// Client response to our ping
case class Pong(token: String) extends SerializableInEvent with AuthenticatedRootMsg

// Client tries to assign name
case class AssignName(name: String, token: String) extends SerializableInEvent with AuthenticatedRootMsg

// Client tries to create room
case class CreateRoom(roomName: String, token: String) extends SerializableInEvent with AuthenticatedRootMsg

// Client tries to join room
case class JoinRoom(roomId: String, token: String) extends SerializableInEvent with RoomMsg

// Client marks himself ready
case class ClientReady(roomId: String, token: String) extends SerializableInEvent with RoomMsg

case class StartGame(roomId: String, token: String) extends SerializableInEvent with RoomMsg

case class TestGameMsg(gameId: String, token: String) extends SerializableInEvent with GameMsg

object SerializableInEvent {
  implicit val assignNameRead = Json.reads[AssignName]
  implicit val joinRoomRead = Json.reads[JoinRoom]
  implicit val createRoomRead = Json.reads[CreateRoom]
  implicit val readyRead = Json.reads[ClientReady]
  implicit val startGameRead = Json.reads[StartGame]
  implicit val pongRead = Json.reads[Pong]
  implicit val listRoomRead = Json.reads[ListRoom]
  implicit val msgToUserRead = Json.reads[MessageToUser]
  implicit val checkNameRead = Json.reads[CheckName]
  implicit val msgToRoomRead = Json.reads[MessageToRoom]

  implicit val testGameMsgRead = Json.reads[TestGameMsg]
  implicit val serializableInEventRead = Json.reads[SerializableInEvent]
}

object OutEvent {
  implicit val notifyClientsChangedWrite = Json.writes[NotifyClientsChanged]
  implicit val notifyRoomsChangedWrite = Json.writes[NotifyRoomsChanged]
  implicit val notifyRoomStatusWrite = Json.writes[NotifyRoomStatus]
  implicit val tokenWrite = Json.writes[Token]
  implicit val okWrite = Json.writes[Ok]
  implicit val createdRoomWrite = Json.writes[CreatedRoom]
  implicit val joinedRoomWrite = Json.writes[JoinedRoom]
  implicit val pingWrite = Json.writes[Ping]
  implicit val errWrite = Json.writes[Err]
  implicit val killWrite = Json.writes[Kill]
  implicit val userMessageWrite = Json.writes[UserMessage]
  implicit val roomMessageWrite = Json.writes[RoomMessage]
  implicit val nameCheckResultWrite = Json.writes[NameCheckResult]

  implicit val notifyGameStateWrite = Json.writes[NotifyGameState]
  implicit val notifyGameStartedWrite = Json.writes[NotifyGameStarted]

  implicit val outEventFormat = Json.writes[OutEvent]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[SerializableInEvent, OutEvent]
}
