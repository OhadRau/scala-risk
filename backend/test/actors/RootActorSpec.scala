package actors

import akka.actor.Props
import akka.testkit.TestProbe
import models._
import org.scalatest.{GivenWhenThen, Matchers, MustMatchers}

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

class RootActorSpec extends TestKitSpec with GivenWhenThen {

  behavior of "RootActor"
  val numClients = 4
  val rootActor = system.actorOf(Props(new RootActor()))
  var clients = new Array[TestProbe](numClients)
  var tokens = new Array[String](numClients)
  val names = new ArrayBuffer[String](numClients)
  var roomId = ""
  it should "accept RegisterClient and reply with an Ok" in {
    for (i <- 0 until numClients) {
      clients(i) = TestProbe(s"client${i}")
    }

    When("the clients are added")
    clients.foreach(client => {
      val registerClient = RegisterClient(Client(), client.ref)
      rootActor ! registerClient
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
      val name: String = ('A' + i).asInstanceOf[Char].toString
      names += name
      rootActor ! AssignName(name, tokens(i))
      clients foreach (client => {
        client.expectMsgPF() {
          case NotifyClientsChanged(clientsSeq: Seq[String]) =>
            names foreach (nameInArray => clientsSeq should contain(nameInArray))
        }
      })
    }
  }

  it should "be able to create a room" in {
    rootActor ! CreateRoom("testRoom", tokens(0))
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
          rooms.head.name should be("testRoom")
          rooms.head.hostName should be(names(0))
          rooms.head.clientStatus should have length 1
          rooms.head.clientStatus.head.name should be (names(0))
          rooms.head.clientStatus.head.status should be (Waiting())
          roomId = rooms.head.roomId
      }
    })
    println(s"roomId: $roomId")
  }

  it should "be able to allow players to join the room" in {
    var numJoined = 1
    val joinedPlayers = new ArrayBuffer[String](numClients)
    joinedPlayers += "A"
    for (i <- 1 until numClients) {
      rootActor ! JoinRoom(roomId, tokens(i))
      joinedPlayers += names(i)
      for (j <- 0 until numClients) {
        if (i == j) {
          clients(j).expectMsg(Ok(roomId))
        }
        clients(j).expectMsgPF() {
          case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
            rooms.head.name should be("testRoom")
            rooms.head.hostName should be(names(0))
            rooms.head.clientStatus foreach (clientStatus => {
              joinedPlayers should contain(clientStatus.name)
              clientStatus.status should be(Waiting())
            })
        }
      }
      numJoined += 1
    }
  }

  it should "allow players to send Ready" in {
    for (i <- 0 until numClients) {
      rootActor ! ClientReady(roomId, tokens(i))
      clients foreach (client => {
        client.expectMsgPF() {
          case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
            for (j <- 0 until numClients) {
              if (j <= i) {
                rooms.head.clientStatus(j).status should be (Ready())
              } else {
                rooms.head.clientStatus(j).status should be (Waiting())
              }
            }
        }
      })
    }
  }

  it should "kill idle clients" in {
    val sacrifice = TestProbe("sacrifice")
    rootActor ! RegisterClient(Client(), sacrifice.ref)
    val sacrificeToken = sacrifice.expectMsgPF() {
      case Ok(token) => token
    }
    rootActor ! AssignName("Sacrifice", sacrificeToken)
    sacrifice.expectMsgPF() {
      case NotifyClientsChanged(clientsSeq: Seq[String]) =>
        clientsSeq should contain("Sacrifice")
    }
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[String]) =>
          clientsSeq should contain("Sacrifice")
      }
    })
    When("a KeepAliveTick is received")
    rootActor ! KeepAliveTick()
    Then("Each client should receive a Ping")
    sacrifice.expectMsg(Ping("Ping"))
    (clients, tokens).zipped.foreach((client, token) => {
      client.expectMsgPF() {
        case _: Ping => rootActor ! Pong(token)
      }
    })
    And("is dropped if they do not respond with a Pong by the next Ping")
    rootActor ! KeepAliveTick()
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
