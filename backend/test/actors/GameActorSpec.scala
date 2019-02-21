package actors

import akka.actor.Props
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import models.{Client, RoomBrief}
import org.scalatest.MustMatchers

import scala.concurrent.duration._
import scala.language.postfixOps

class GameActorSpec extends TestKitSpec with MustMatchers {

  "GameActor" should {
    "Accept a RegisterClient and reply with an Ok" in {
      val userActor = TestProbe("userActor")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerClient = RegisterClient(Client(), userActor.ref)
      gameActor ! registerClient
      val token = userActor.expectMsgPF() {
        case Ok(token) => token
      }
      println(s"Got token: $token")
    }

    "Accept multiple RegisterClients and reply with different tokens" in {
      val user1 = TestProbe("user1")
      val user2 = TestProbe("user2")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerClient1 = RegisterClient(Client(), user1.ref)
      gameActor ! registerClient1
      val token1 = user1.expectMsgPF() {
        case Ok(token) => token
      }
      val registerClient2 = RegisterClient(Client(), user2.ref)
      gameActor ! registerClient2
      val token2 = user2.expectMsgPF() {
        case Ok(token) => token
      }
      assert(token1 != token2)
    }

    "Be able to assign names to a user" in {
      val user1 = TestProbe("user1")
      val user2 = TestProbe("user2")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerClient1 = RegisterClient(Client(), user1.ref)
      gameActor ! registerClient1
      val token1 = user1.expectMsgPF() {
        case Ok(token) => token
      }
      val registerClient2 = RegisterClient(Client(), user2.ref)
      gameActor ! registerClient2
      val token2 = user2.expectMsgPF() {
        case Ok(token) => token
      }
      assert(token1 != token2)

      val assignName = AssignName("Oswin", token1)
      gameActor ! assignName
      user1.expectMsgPF() {
        case NotifyClientsChanged(clients: Seq[String]) => assert(clients.contains("Oswin"))
      }
      user2.expectMsgPF() {
        case NotifyClientsChanged(clients: Seq[String]) => assert(clients.contains("Oswin"))
      }
    }

    "Be able to create rooms" in {
      val user1 = TestProbe("user1")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerClient1 = RegisterClient(Client(), user1.ref)
      gameActor ! registerClient1
      val token1 = user1.expectMsgPF() {
        case Ok(token) => token
      }

      val assignName = AssignName("Oswin", token1)
      gameActor ! assignName
      user1.expectMsgPF() {
        case NotifyClientsChanged(clients: Seq[String]) => assert(clients.contains("Oswin"))
      }

      val createRoom = CreateRoom("testRoom", token1)
      gameActor ! createRoom
      user1.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) => assert(
          rooms.exists(roomBrief => roomBrief.name == "testRoom"
            && roomBrief.hostName == "Oswin" && roomBrief.clientCount == 1))
      }
    }

    "kill idle clients" in {
      val user1 = TestProbe("user1")
      val user2 = TestProbe("user2")

      val gameActor = system.actorOf(Props(new GameActor()))
      val registerClient1 = RegisterClient(Client(), user1.ref)
      gameActor ! registerClient1
      val token1 = user1.expectMsgPF() {
        case Ok(token) => token
      }
      val registerClient2 = RegisterClient(Client(), user2.ref)
      gameActor ! registerClient2
      val token2 = user2.expectMsgPF() {
        case Ok(token) => token
      }
      assert(token1 != token2)

      val assignName1 = AssignName("Oswin", token1)
      gameActor ! assignName1
      user1.expectMsgPF() {
        case NotifyClientsChanged(clients: Seq[String]) => assert(clients.contains("Oswin"))
      }
      user2.expectMsgPF() {
        case NotifyClientsChanged(clients: Seq[String]) => assert(clients.contains("Oswin"))
      }
      val assignName2 = AssignName("Ohad", token2)
      gameActor ! assignName2
      user1.expectMsgPF() {
        case NotifyClientsChanged(clients: Seq[String]) => assert(clients.contains("Oswin") && clients.contains("Ohad"))
      }
      user2.expectMsgPF() {
        case NotifyClientsChanged(clients: Seq[String]) => assert(clients.contains("Oswin") && clients.contains("Ohad"))
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
            case NotifyClientsChanged(clients: Seq[String]) =>
              assert(!clients.contains("Oswin") && clients.contains("Ohad"))
          }
      }
      user1.expectMsg(Kill("Killed for inactivity"))
    }
  }
}
