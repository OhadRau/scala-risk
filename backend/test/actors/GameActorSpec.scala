package actors

import akka.actor.Props
import akka.testkit.TestProbe
import models.{Client, RoomBrief}
import org.scalatest.{GivenWhenThen, Matchers, MustMatchers}

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

class GameActorSpec extends TestKitSpec with GivenWhenThen {

  behavior of "GameActor"
  val numClients = 4
  val gameActor = system.actorOf(Props(new GameActor()))
  var clients = new Array[TestProbe](numClients)
  var tokens = new Array[String](numClients)
  val names = new ArrayBuffer[String](numClients)
  it should "accept RegisterClient and reply with an Ok" in {
    for(i <- 0 until numClients) {
      clients(i) = TestProbe(s"client${i}")
    }

    When("the clients are added")
    clients.foreach(client => {
      val registerClient = RegisterClient(Client(), client.ref)
      gameActor ! registerClient
    })

    Then("each client should receive a token")
    tokens = clients.map(client => {
      client.expectMsgPF() {
        case Ok(token) => token
      }
    })

    And("each token is unique")
    tokens.toSet should have size tokens.length
  }

  it should "be able to assign names to a client broadcast change to all clients" in {
    for (i <- 0 until numClients) {
      val name: String = ('A'+i).asInstanceOf[Char].toString
      names += name
      gameActor ! AssignName(name, tokens(i))
      clients foreach (client => {
        client.expectMsgPF() {
          case NotifyClientsChanged(clientsSeq: Seq[String]) =>
            names foreach (nameInArray => clientsSeq should contain (nameInArray) )
          }
      })
    }
  }

  it should "be able to create a room" in {
    val createRoom = CreateRoom("testRoom", tokens(0))
    gameActor ! createRoom
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
          rooms.exists(roomBrief => {
            roomBrief.name == "testRoom" && roomBrief.hostName == names(0) && roomBrief.clientCount == 1
          })
      }
    })
  }

  it should "kill idle clients" in {
    val sacrifice = TestProbe("sacrifice")
    gameActor ! RegisterClient(Client(), sacrifice.ref)
    val sacrificeToken = sacrifice.expectMsgPF() {
      case Ok(token) => token
    }
    gameActor ! AssignName("Sacrifice", sacrificeToken)
    sacrifice.expectMsgPF() {
      case NotifyClientsChanged(clientsSeq: Seq[String]) =>
        clientsSeq should contain ("Sacrifice")
    }
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[String]) =>
          clientsSeq should contain ("Sacrifice")
      }
    })
    When("a KeepAliveTick is received")
    gameActor ! KeepAliveTick()
    Then("Each client should receive a Ping")
    sacrifice.expectMsg(Ping("Ping"))
    (clients, tokens).zipped.foreach ((client, token) => {
      client.expectMsgPF() {
        case _: Ping => gameActor ! Pong(token)
      }
    })
    And("is dropped if they do not respond with a Pong by the next Ping")
    gameActor ! KeepAliveTick()
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[String]) =>
          clientsSeq should not contain ("Sacrifice")
      }
    })
    And("the dropped client should receive a Kill message")
    sacrifice.expectMsg(Kill("Killed for inactivity"))
  }
}
