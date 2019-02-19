package controllers

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.{ActorMaterializer, FlowShape, Materializer, OverflowStrategy}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import javax.inject._

import scala.language.implicitConversions
import actors._
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import models.Player

class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  val logger = play.api.Logger(getClass)

  def ws = WebSocket.accept[SerializableInEvent, OutEvent] { request =>
    gameFlow
  }

  private val gameActor = actorSystem.actorOf(Props(new GameActor))
  private val playerActorSource = Source.actorRef[OutEvent](5, OverflowStrategy.fail)
/**
*                            +-------+
*                            |       |
*                            |       |
*                            |       |     +----------------+    +------------+
*              +---------+   |       |     |                | +->-|ActorRef   |
*WebSocket+--->+playerMsg+-> |       |     |                |    +------------+
*              +---------+   |       |     |                |
*                            | Merge |     |                |    +------------+
*                            |       +---->+ Sink  GameActor| +->-|ActorRef   |
*+--------------------+      |       |     |                |    +------------+
*|ActorRef for player | +--> |       |     |                |
*|(Only on connection)|      |       |     |                |
*+--------------------+      |       |     |                |     +----------+
*                            |       |     |                | +-->-|ActorRef |
*                            |       |     +----------------+     +----------+
*                            |       |
*                            |       |
*                            +-------+
**/



  private val gameFlow: Flow[SerializableInEvent, OutEvent, Any] = Flow.fromGraph(GraphDSL.create(playerActorSource) {
    implicit builder => playerActor =>
      import GraphDSL.Implicits._

      val materialization = builder.materializedValue.map(playerActorRef => RegisterPlayer(Player(), playerActorRef))
      val playerMsg: FlowShape[InEvent, InEvent] = builder.add(Flow[InEvent])
      val merge = builder.add(Merge[InEvent](2))
      val gameActorSink = Sink.actorRef[InEvent](gameActor, None)
      materialization ~> merge
      playerMsg ~> merge
      merge ~> gameActorSink

      FlowShape(playerMsg.in, playerActor.out)
  })
}
