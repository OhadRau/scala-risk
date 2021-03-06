package actors

import akka.actor.Props
import akka.testkit.TestProbe
import models._
import org.scalatest.{GivenWhenThen, Matchers, MustMatchers}

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

class RootActorSpec extends TestKitSpec with GivenWhenThen {

  val logger = play.api.Logger(getClass)

  behavior of "Core Game Logic"
  val numClients = 4
  val rootActor = system.actorOf(Props(new RootActor()))
  var clients = new Array[TestProbe](numClients)
  var tokens = new Array[String](numClients)
  var publicTokens = new Array[String](numClients)
  val names = new ArrayBuffer[String](numClients)
  var hostToken = ""
  var roomId = ""
  it should "accept RegisterClient and reply with a Token" in {
    for (i <- 0 until numClients) {
      clients(i) = TestProbe(s"client$i")
    }

    When("the clients are added")
    clients.foreach(client => {
      val registerClient = RegisterClient(Client(), client.ref)
      rootActor ! registerClient
    })

    Then("each client should receive a token and a publicToken")
    val tup = clients.map(client => {
      client.expectMsgPF() {
        case Token(token, publicToken) => (token, publicToken)
      }
    })
    tokens = tup.unzip._1
    publicTokens = tup.unzip._2

    And("each token and publicToken is unique")
    tokens.toSet should have size tokens.length
    publicTokens.toSet should have size publicTokens.length
  }

  it should "reject invalid names such as empty string" in {
    rootActor ! AssignName("", tokens(0))
    clients(0).expectMsg(NameAssignResult(success = false, "", "Name is not unique!"))
  }

  it should "be able to assign names to a client and broadcast change to all clients" in {
    for (i <- 0 until numClients) {
      val name: String = ('A' + i).asInstanceOf[Char].toString
      names += name
      rootActor ! AssignName(name, tokens(i))
      for (j <- 0 until numClients) {
        if (i == j) {
          clients(j).expectMsg(NameAssignResult(success = true, name))
        }
        clients(j).expectMsgPF() {
          case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
            names foreach (nameInArray => clientsSeq.exists(clientBrief => clientBrief.name == nameInArray))
        }
      }
    }
  }

  it should "reject multiple attempts to assign the same name to a client" in {
    rootActor ! AssignName("A", tokens(0))
    clients(0).expectMsg(NameAssignResult(success = false, "A", "Name already assigned!"))
  }

  it should "be able to create a room" in {
    rootActor ! CreateRoom("testRoom", tokens(0))
    roomId = clients(0).expectMsgPF() {
      case CreatedRoom(id) => id
    }
    clients(0).expectMsg(JoinedRoom(roomId, publicTokens(0)))
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
          rooms.head.name should be("testRoom")
          rooms.head.hostToken should be(publicTokens(0))
          rooms.head.numClients should be(1)
      }
    })
    clients(0).expectMsgPF() {
      case NotifyRoomStatus(roomStatus: RoomStatus) =>
        logger.debug("NotifyRoomStatus")
        roomStatus.name should be("testRoom")
        roomStatus.hostName should be(names(0))
        roomStatus.clientStatus foreach (clientStatus => {
          clientStatus.name should be(names(0))
          clientStatus.status should be(Waiting())
        })
    }
    logger.debug(s"roomId: $roomId")
  }

  it should "allow players to join the room" in {
    var numJoined = 1
    val joinedPlayers = new ArrayBuffer[String](numClients)
    joinedPlayers += "A"
    for (i <- 1 until numClients) {
      rootActor ! JoinRoom(roomId, tokens(i))
      joinedPlayers += names(i)
      numJoined += 1
      for (j <- 0 until numClients) {
        if (j <= i) {
          for (_ <- 0 until 2) {
            clients(j).expectMsgPF() {
              case NotifyRoomStatus(roomStatus: RoomStatus) =>
                roomStatus.name should be("testRoom")
                roomStatus.hostName should be(names(0))
                roomStatus.clientStatus foreach (clientStatus => {
                  joinedPlayers should contain(clientStatus.name)
                  clientStatus.status should be(Waiting())
                })
              case JoinedRoom(id, public) =>
                id should be(roomId)
                public should be(publicTokens(i))
            }
          }
        }
        clients(j).expectMsgPF() {
          case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
            rooms.head.name should be("testRoom")
            rooms.head.hostToken should be(publicTokens(0))
            rooms.head.numClients should equal(numJoined)
        }
      }
    }
  }

  it should "allow players to leave the room" in {
    rootActor ! LeaveRoom(roomId, tokens(1))
    logger.info("1")
    clients(1).expectMsgPF() {
      case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
        rooms.head.numClients should equal (numClients - 1)
    }
    logger.info("2")
    for (i <- Seq(0, 2, 3)) {
      for (_ <- 0 until 2) {
        clients(i).expectMsgPF() {
          case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
            rooms.head.numClients should equal (numClients - 1)
          case NotifyRoomStatus(status: RoomStatus) =>
            status.clientStatus.length should equal (numClients - 1)
        }
      }
    }
  }

  it should "reassign a host if the host leaves the room" in {
    rootActor ! LeaveRoom(roomId, tokens(0))
    clients(0).expectMsgPF() {
      case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
        rooms.head.numClients should equal (numClients - 2)
    }
    clients(1).expectMsgPF() {
      case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
        rooms.head.numClients should equal (numClients - 2)
    }
    for (i <- Seq(2, 3)) {
      for (_ <- 0 until 2) {
        clients(i).expectMsgPF() {
          case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
            rooms.head.numClients should equal (numClients - 2)
          case NotifyRoomStatus(status: RoomStatus) =>
            status.hostName should not be (names(0))
            hostToken = tokens(names.indexOf(status.hostName))
            status.clientStatus.length should equal (numClients - 2)
        }
      }
    }
  }

  it should "allow players who left to join the room again" in {
    rootActor ! JoinRoom(roomId, tokens(1))
    for (i <- 1 until numClients) {
      for (_ <- 0 until 2) {
        clients(i).expectMsgPF() {
          case NotifyRoomStatus(roomStatus: RoomStatus) =>
            roomStatus.name should be("testRoom")
            roomStatus.clientStatus.size should be(clients.length - 1)
          case JoinedRoom(id, public) =>
            id should be(roomId)
        }
      }
    }
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
          rooms.head.name should be("testRoom")
          rooms.head.numClients should equal(clients.length - 1)
      }
    })
    logger.info("3")
    rootActor ! JoinRoom(roomId, tokens(0))
    clients foreach (client => {
      for (_ <- 0 until 2) {
        client.expectMsgPF() {
          case NotifyRoomStatus(roomStatus: RoomStatus) =>
            roomStatus.name should be("testRoom")
            roomStatus.clientStatus.size should be(clients.length)
          case JoinedRoom(id, public) =>
            id should be(roomId)
        }
      }
    })
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
          rooms.head.name should be("testRoom")
          rooms.head.numClients should equal(clients.length)
      }
    })
  }

  it should "not allow the host to start without everyone being Ready" in {
    rootActor ! StartGame(roomId, hostToken)
    clients(tokens.indexOf(hostToken)).expectMsg(Err("Not everyone in the room is ready."))
  }

  it should "allow players to send Ready" in {
    for (i <- 0 until numClients) {
      rootActor ! ClientReady(roomId, tokens(i))
      clients foreach (client => {
        client.expectMsgPF() {
          case NotifyRoomStatus(roomStatus: RoomStatus) =>
            for (j <- 0 until numClients) {
              if (j <= i) {
                roomStatus.clientStatus contains ClientStatus(names(j), Ready(), publicTokens(j))
              } else {
                roomStatus.clientStatus contains ClientStatus(names(j), Waiting(), publicTokens(j))
              }
            }
        }
      })
    }
  }

  it should "not allow a client who is not the host to start the game" in {
    rootActor ! StartGame(roomId, tokens(0))
    clients(0).expectMsg(Err("You are not the host."))
  }

  it should "allow the host to start the game" in {
    rootActor ! StartGame(roomId, hostToken)
    clients foreach (client => {
      for(i <- 0 until 3) {
        client.ignoreMsg{case NotifyTurn(_) => true}
        client.expectMsgPF() {
          case NotifyGameStarted(state) => state.players foreach (player => {
            names should contain(player.name)
            player.unitCount should be((10 - numClients) * 5)
          })
          case NotifyRoomsChanged(rooms: Seq[RoomBrief]) =>
            rooms.size should be(0)
          case SendMapResource(_) =>
        }
      }
    })
    clients foreach (client => {
      client.expectNoMessage()
    })
  }

  it should "kill idle clients" in {
    val sacrifice = TestProbe("sacrifice")
    rootActor ! RegisterClient(Client(), sacrifice.ref)
    val sacrificeToken = sacrifice.expectMsgPF() {
      case Token(token, publicToken) => token
    }
    rootActor ! AssignName("Sacrifice", sacrificeToken)
    sacrifice.expectMsg(NameAssignResult(success = true, "Sacrifice"))
    sacrifice.expectMsgPF() {
      case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
        clientsSeq exists (clientBrief => clientBrief.name == "Sacrifice") should be(true)
    }
    clients foreach (client => {
      client.expectMsgPF() {
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq exists (clientBrief => clientBrief.name == "Sacrifice") should be(true)
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
        case NotifyClientsChanged(clientsSeq: Seq[ClientBrief]) =>
          clientsSeq exists (clientBrief => clientBrief.name == "Sacrifice") should be(false)
      }
    })
    And("the dropped client should receive a Kill message")
    sacrifice.expectMsg(Kill("Killed for inactivity"))
  }

  it should "allow sending a token to relogin" in {
    val newClient = TestProbe("newClient")
    rootActor ! RegisterClient(Client(), newClient.ref)
    val tup = newClient.expectMsgPF() {
      case Token(token, publicToken) => (token, publicToken)
    }
    val newClientToken = tup._1
    rootActor ! SetToken(newClientToken, tokens(0))
    newClient.expectMsgPF() {
      case Token(token, publicToken) =>
        token should be (tokens(0))
        publicToken should be (publicTokens(0))
    }
  }
}
