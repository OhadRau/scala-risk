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
      }
    })
    rootActor ! AssignName("B", tokens(1))
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq exists (clientBrief => clientBrief.name == "A") should be (true)
          clientsSeq exists (clientBrief => clientBrief.name == "B") should be (true)
          clientsSeq foreach { brief =>
            if (brief.name == "A") publicToken(0) = brief.publicToken
            if (brief.name == "B") publicToken(1) = brief.publicToken
          }
      }
    })

    Then("B can send a message to A using A's public token")
    val message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ornare ut arcu non venenatis. Donec nec suscipit turpis, ac congue"
    rootActor ! MessageToUser(tokens(1), publicToken(0), message)
    logger.debug("0")
    clients(1).expectMsg(UserMessage("B", message))
    logger.debug("1")
    clients(0).expectMsg(UserMessage("B", message))
    logger.debug("2")
  }
  it should "be able to send and receive a lot of messages" in {
    val r = new Random
    for (i <- 0 until 100000) {
      val sender = r.nextInt(1)
      val name = if(sender == 0) "A" else "B"
      val message = Random.alphanumeric take 20 mkString

      rootActor ! MessageToUser(tokens(sender), publicToken(-sender+1), message)
      clients(0).expectMsg(UserMessage(name, message))
      clients(1).expectMsg(UserMessage(name, message))
    }
  }
}
