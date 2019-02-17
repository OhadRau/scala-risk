package actors

import akka.actor.ActorRef
import models.{GameState, Player}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.language.implicitConversions

trait GameMessage
trait GameMessageIn extends GameMessage {
  // TODO: Add Reads here
}
trait GameMessageOut extends GameMessage {
  def json[T <: GameMessageOut]: JsValue
}

// Received Messages
case class PlayerJoined(name: String, token: String) extends GameMessageIn
case class GenerateToken(player: Player, actor: ActorRef) extends GameMessageIn

// Sent Messages
case class RequestName(token: String) extends GameMessageOut {
  override def json[RequestName]: JsValue = Json.obj(
      "type" -> "name",
      "token" -> token
  )
}

case class NotifyGameState(state: GameState) extends GameMessageOut {
  implicit val write: Writes[NotifyGameState] = new Writes[NotifyGameState] {
    override def writes(o: NotifyGameState): JsValue = Json.obj(
      "type" -> "state"
    )
  }

  override def json[NotifyGameState]: JsValue = Json.obj(
    "type" -> "state"
  )
}

object PlayerJoined {
  def apply(name: String, token: String): PlayerJoined = new PlayerJoined(name, token)
}

object GameMessage extends Enumeration {
  type GameMessage = Value
  val PlayerJoinedType, GenerateTokenType, NotifyGameStateType = Value

  implicit val playerJoinedReads: Reads[PlayerJoined] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "token").read[String]
  )(PlayerJoined.apply _)

  implicit def string2GameMessage(str: String): GameMessage = {
    str match {
      case "PlayerJoined" => PlayerJoinedType
      case "GenerateToken" => GenerateTokenType
    }
  }
}

