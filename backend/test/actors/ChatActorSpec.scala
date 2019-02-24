package actors

import akka.actor.Props
import akka.testkit.TestProbe
import models._
import org.scalatest.{GivenWhenThen, Matchers, MustMatchers}
import scala.util.Random
import scala.concurrent.duration._

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps


class ChatActorSpec extends TestKitSpec with GivenWhenThen {
  val logger = play.api.Logger(getClass)

  behavior of "Chatting"
  val rootActor = system.actorOf(Props(new RootActor()))
  var clients = new Array[TestProbe](2)
  var tokens = new Array[String](2)
  val names = new ArrayBuffer[String](2)
  var publicToken = new Array[String](2)
  for (i <- 0 until 2) {
    clients(i) = TestProbe(s"client${i}")
  }
  it should "allow a user to send a message to another user with their public token" in {
    Given("the clients are registered")
    clients foreach (client => {
      val registerClient = RegisterClient(Client(), client.ref)
      rootActor ! registerClient
    })

    And("and have tokens")
    tokens = clients.map(client => {
      client.expectMsgPF() {
        case Token(token) => token
      }
    })

    And("have names")
    rootActor ! AssignName("A", tokens(0))
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq exists (clientBrief => clientBrief.name == "A") should be (true)
          publicToken(0) = clientsSeq.head.publicToken
      }
    })
    rootActor ! AssignName("B", tokens(1))
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq exists (clientBrief => clientBrief.name == "A") should be (true)
          clientsSeq exists (clientBrief => clientBrief.name == "B") should be (true)
          publicToken(1) = if (clientsSeq.head.publicToken != publicToken(0)) clientsSeq.head.publicToken else clientsSeq(1).publicToken
      }
    })

    Then("B can send a message to A using A's public token")
    var message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ornare ut arcu non venenatis. Donec nec suscipit turpis, ac congue"
    rootActor ! MessageToUser(tokens(1), publicToken(1), message)
    clients(0).fishForSpecificMessage(){case UserMessage("B", message) => }
    clients(1).fishForSpecificMessage(){case UserMessage("B", message) => }
  }
  it should "be able to send and receive a lot of messages" in {
    val r = new Random
    for (i <- 0 until 100) {
      var sender = r.nextInt(1)
      var name = if(sender == 0) "A" else "B"
      var message = Random.alphanumeric take 20 mkString

      rootActor ! MessageToUser(tokens(sender), publicToken(sender), message)
      clients(0).expectMsg(10 seconds, UserMessage(name, message))
      clients(1).expectMsg(10 seconds, UserMessage(name, message))
    }
  }
}
