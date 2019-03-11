package actors

import akka.actor.Props
import akka.testkit.TestProbe
import models._
import org.scalatest.{GivenWhenThen, Ignore, Matchers, MustMatchers}

import scala.util.Random
import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

class ChatActorSpec extends TestKitSpec with GivenWhenThen {
  val logger = play.api.Logger(getClass)

  behavior of "Chatting"
  val rootActor = system.actorOf(Props(new RootActor()))
  var clients = new ArrayBuffer[TestProbe](2)
  var tokens = new ArrayBuffer[String](2)
  val names = new ArrayBuffer[String](2)
  var publicToken = new ArrayBuffer[String](2)
  var roomId = ""

  for (i <- 0 until 2) {
    clients append TestProbe(s"client$i")
  }

  it should "allow a user to send a message to another user with their public token" in {
    Given("the clients are registered")
    clients foreach (client => {
      rootActor ! RegisterClient(Client(), client.ref)
    })

    And("and have tokens")
    tokens = clients.map(client => {
      client.expectMsgPF() {
        case Token(token, publicToken) => token
      }
    })

    And("have names")
    rootActor ! AssignName("A", tokens(0))
    names append "A"
    clients(0).expectMsg(NameAssignResult(success = true, "A"))
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq exists (clientBrief => clientBrief.name == "A") should be(true)
      }
    })
    rootActor ! AssignName("B", tokens(1))
    names append "B"
    clients(1).expectMsg(NameAssignResult(success = true, "B"))
    publicToken append ""
    publicToken append ""
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq exists (clientBrief => clientBrief.name == "A") should be(true)
          clientsSeq exists (clientBrief => clientBrief.name == "B") should be(true)
          clientsSeq foreach { brief =>
            if (brief.name == "A") publicToken(0) = brief.publicToken
            if (brief.name == "B") publicToken(1) = brief.publicToken
          }
      }
    })

    Then("B can send a message to A using A's public token")
    val message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ornare ut arcu non venenatis. Donec " +
      "nec suscipit turpis, ac congue"
    rootActor ! MessageToUser(tokens(1), publicToken(0), message)
    clients(1).expectMsgPF() {
      case msg: UserMessage =>
        msg.message should be(message)
        msg.publicToken should be(publicToken(1))
        msg.senderName should be("B")
    }
    clients(0).expectMsgPF() {
      case msg: UserMessage =>
        msg.message should be(message)
        msg.publicToken should be(publicToken(1))
        msg.senderName should be("B")
    }
  }

  it should "be able to send and receive a lot of messages" in {
    val r = new Random
    for (_ <- 0 until 100000) {
      val sender = r.nextInt(1)
      val name = names(sender)
      val message = Random.alphanumeric take 20 mkString

      rootActor ! MessageToUser(tokens(sender), publicToken(-sender + 1), message)
      clients(1).expectMsgPF() {
        case msg: UserMessage =>
          msg.message should be(message)
          msg.publicToken should be(publicToken(sender))
          msg.senderName should be(name)
      }
      clients(0).expectMsgPF() {
        case msg: UserMessage =>
          msg.message should be(message)
          msg.publicToken should be(publicToken(sender))
          msg.senderName should be(name)
      }
    }
  }

  it should "be able to send message to a room" in {
    Given("there are now 3 clients")
    clients append TestProbe("client3")
    rootActor ! RegisterClient(Client(), clients(2).ref)
    tokens append clients(2).expectMsgPF() {
      case Token(token, publicToken) => token
    }

    rootActor ! AssignName("C", tokens(2))
    clients(2).expectMsg(NameAssignResult(success = true, "C"))
    names append "C"
    publicToken append ""
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq foreach { brief =>
            if (brief.name == "C") publicToken(2) = brief.publicToken
          }
      }
    })

    And("a room is created")
    rootActor ! CreateRoom("testRoom", tokens(0))
    roomId = clients(0).expectMsgPF() {
      case CreatedRoom(id) => id
    }
    clients(0).expectMsg(JoinedRoom(roomId, publicToken(0)))
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
          rooms.head.name should be("testRoom")
          rooms.head.hostToken should be(publicToken(0))
          rooms.head.numClients should be(1)
      }
    })

    And("they join all the room")
    rootActor ! JoinRoom(roomId, tokens(1))
    rootActor ! JoinRoom(roomId, tokens(2))
    // Flush all messages
    clients foreach (client => client.receiveWhile() { case _ => client.msgAvailable })

    Then("they can send messages to the room")
    val message = "Lorem ipsum"
    rootActor ! MessageToRoom(tokens(0), roomId, message)
    clients foreach (client => {
      client.expectMsgPF() {
        case msg: RoomMessage =>
          msg.message should be(message)
          msg.senderName should be(names(0))
      }
    })
  }

  it should "be able to send a lot of messages in the room" in {
    val r = new Random
    for (_ <- 0 until 100000) {
      val sender = r.nextInt(1)
      val name = names(sender)
      val message = Random.alphanumeric take 20 mkString

      rootActor ! MessageToRoom(tokens(sender), roomId, message)
      clients foreach (client => {
        client.expectMsgPF() {
          case msg: RoomMessage =>
            msg.message should be(message)
            msg.senderName should be(name)
        }
      })
    }
  }
}
