package actors

import java.awt.event.InputEvent

import akka.actor.Props
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import models.Player
import org.scalatest.MustMatchers
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

class GameActorSpec extends TestKitSpec with MustMatchers {

  "GameActor" should {
    "Accept a RegisterPlayer and reply with an Ok" in {
      val userActor = TestProbe("userActor")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerPlayer = RegisterPlayer(Player(), userActor.ref)
      gameActor ! registerPlayer
      val token = userActor.expectMsgPF() {
        case Ok(token) => token
      }
      println(s"Got token: $token")
    }

    "Accept multiple RegisterPlayers and reply with different tokens" in {
      val user1 = TestProbe("user1")
      val user2 = TestProbe("user2")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerPlayer1 = RegisterPlayer(Player(), user1.ref)
      gameActor ! registerPlayer1
      val token1 = user1.expectMsgPF() {
        case Ok(token) => token
      }
      val registerPlayer2 = RegisterPlayer(Player(), user2.ref)
      gameActor ! registerPlayer2
      val token2 = user2.expectMsgPF() {
        case Ok(token) => token
      }
      assert(token1 != token2)
    }

    "Be able to assign names to a user" in {
      val user1 = TestProbe("user1")
      val user2 = TestProbe("user2")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerPlayer1 = RegisterPlayer(Player(), user1.ref)
      gameActor ! registerPlayer1
      val token1 = user1.expectMsgPF() {
        case Ok(token) => token
      }
      val registerPlayer2 = RegisterPlayer(Player(), user2.ref)
      gameActor ! registerPlayer2
      val token2 = user2.expectMsgPF() {
        case Ok(token) => token
      }
      assert(token1 != token2)

      val assignName = AssignName("Oswin", token1)
      gameActor ! assignName
      user1.expectMsgPF() {
        case NotifyPlayerJoined(players: Seq[String]) => assert(players.contains("Oswin"))
      }
      user2.expectMsgPF() {
        case NotifyPlayerJoined(players: Seq[String]) => assert(players.contains("Oswin"))
      }
    }
  }
}
