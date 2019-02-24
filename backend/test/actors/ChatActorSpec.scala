package actors

import akka.actor.Props
import akka.testkit.TestProbe
import models._
import org.scalatest.{GivenWhenThen, Matchers, MustMatchers}

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps


class ChatActorSpec extends TestKitSpec with GivenWhenThen {
  val logger = play.api.Logger(getClass)

  behavior of "Chatting"
  val rootActor = system.actorOf(Props(new RootActor()))
  var clients = new Array[TestProbe](2)
  var tokens = new Array[String](2)
  val names = new ArrayBuffer[String](2)
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
      }
    })
  }
}
