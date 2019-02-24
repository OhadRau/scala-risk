package controllers

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream._
import play.api.mvc.{AbstractController, ControllerComponents, RequestHeader, WebSocket}
import javax.inject._

import scala.language.{implicitConversions, postfixOps}
import actors._
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import models.Client

class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  val logger = play.api.Logger(getClass)

  def ws = WebSocket.accept[SerializableInEvent, OutEvent] { request: RequestHeader =>
    gameFlow
  }

  private val gameActor = actorSystem.actorOf(Props(new RootActor))
  private val clientActorSource = Source.actorRef[OutEvent](50, OverflowStrategy.dropTail)
/**
*                            +-------+
*                            |       |
*                            |       |
*                            |       |     +----------------+    +------------+
*              +---------+   |       |     |                | +->-|ActorRef   |
*WebSocket+--->+clientMsg+-> |       |     |                |    +------------+
*              +---------+   |       |     |                |
*                            | Merge |     |                |    +------------+
*                            |       +---->+ Sink  GameActor| +->-|ActorRef   |
*+--------------------+      |       |     |                |    +------------+
*|ActorRef for client | +--> |       |     |                |
*|(Only on connection)|      |       |     |                |
*+--------------------+      |       |     |                |     +----------+
*                            |       |     |                | +-->-|ActorRef |
*                            |       |     +----------------+     +----------+
*                            |       |
*                            |       |
*                            +-------+
**/

  private val gameFlow = Flow.fromGraph(GraphDSL.create(clientActorSource) {
    implicit builder => clientActor =>
      import GraphDSL.Implicits._

      val materialization = builder.materializedValue.map(clientActorRef => RegisterClient(Client(), clientActorRef))
      val clientMsg: FlowShape[InEvent, InEvent] = builder.add(Flow[InEvent])
      // TODO: Change this to outside the flow, so that only one for all clients
      val merge = builder.add(Merge[InEvent](2))
      val gameActorSink = Sink.actorRef[InEvent](gameActor, None)
      materialization ~> merge
      clientMsg ~> merge
      merge ~> gameActorSink

      FlowShape(clientMsg.in, clientActor.out)
  })
}


