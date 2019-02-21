package actors

import akka.actor.Props
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import models.{Player, RoomBrief}
import org.scalatest.MustMatchers

import scala.concurrent.duration._
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
        case NotifyPlayersChanged(players: Seq[String]) => assert(players.contains("Oswin"))
      }
      user2.expectMsgPF() {
        case NotifyPlayersChanged(players: Seq[String]) => assert(players.contains("Oswin"))
      }
    }

    "Be able to create rooms" in {
      val user1 = TestProbe("user1")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerPlayer1 = RegisterPlayer(Player(), user1.ref)
      gameActor ! registerPlayer1
      val token1 = user1.expectMsgPF() {
        case Ok(token) => token
      }

      val assignName = AssignName("Oswin", token1)
      gameActor ! assignName
      user1.expectMsgPF() {
        case NotifyPlayersChanged(players: Seq[String]) => assert(players.contains("Oswin"))
      }

      val createRoom = CreateRoom("testRoom", token1)
      gameActor ! createRoom
      user1.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) => assert(
          rooms.exists(roomBrief => roomBrief.name == "testRoom"
            && roomBrief.hostName == "Oswin" && roomBrief.playerCount == 1))
      }
    }

    "kill idle clients" in {
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

      val assignName1 = AssignName("Oswin", token1)
      gameActor ! assignName1
      user1.expectMsgPF() {
        case NotifyPlayersChanged(players: Seq[String]) => assert(players.contains("Oswin"))
      }
      user2.expectMsgPF() {
        case NotifyPlayersChanged(players: Seq[String]) => assert(players.contains("Oswin"))
      }
      val assignName2 = AssignName("Ohad", token2)
      gameActor ! assignName2
      user1.expectMsgPF() {
        case NotifyPlayersChanged(players: Seq[String]) => assert(players.contains("Oswin") && players.contains("Ohad"))
      }
      user2.expectMsgPF() {
        case NotifyPlayersChanged(players: Seq[String]) => assert(players.contains("Oswin") && players.contains("Ohad"))
      }

      val user2Pong = Pong(token2)
      val keepAlive = KeepAliveTick()
      gameActor ! keepAlive
      user1.expectMsgPF(1.seconds) {
        case _: Ping => ()
      }
      user2.expectMsgPF(1.seconds) {
        case _: Ping =>
          gameActor ! user2Pong
          gameActor ! keepAlive
          user2.expectMsgPF(1.seconds) {
            case NotifyPlayersChanged(players: Seq[String]) =>
              assert(!players.contains("Oswin") && players.contains("Ohad"))
          }
      }
      user1.expectMsg(Kill("Killed for inactivity"))
    }
  }
}
