package controllers

import actors._
import akka.actor.{ActorSystem, InvalidMessageException, Props}
import akka.stream.{FlowShape, Materializer, OverflowStrategy}
import akka.stream.scaladsl._
import javax.inject._
import models.Player
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.language.implicitConversions

class GameController @Inject()(cc: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  val logger = play.api.Logger(getClass)

  def ws: WebSocket = WebSocket.accept[JsValue, JsValue] { rh =>
    gameFlow
  }

  val gameActor = system.actorOf(Props(new GameActor))
  private val playerActorSource = Source.actorRef[GameMessageOut](5, OverflowStrategy.fail)

  private val gameFlow: Flow[JsValue, JsValue, Any] = Flow.fromGraph(GraphDSL.create(playerActorSource) {
    implicit builder => playerActor =>
      import GraphDSL.Implicits._

      val materialization = builder.materializedValue.map(playerActorRef => Some(GenerateToken(Player(), playerActorRef)))
      val merge = builder.add(Merge[Option[GameMessageIn]](2))

      val jsonToGameMessageFlow: FlowShape[JsValue, Option[GameMessageIn]] = builder.add(Flow[JsValue].map {
        case jsMsg: JsValue =>
          logger.debug(s"Received message: ${jsMsg.toString()}")
          (jsMsg \ "message_type").as[String] match {
            case message_type => GameMessage.string2GameMessage(message_type) match {
              case GameMessage.PlayerJoinedType =>
                jsMsg.validate[PlayerJoined] match {
                  case s: JsSuccess[PlayerJoined] => Some(s.get)
                  case e: JsError => None
                }
            }
          }
      })
      val gameActorSink = Sink.actorRef[Option[GameMessageIn]](gameActor, None)
      materialization ~> merge ~> gameActorSink
      jsonToGameMessageFlow ~> merge

      val gameMessageToJsonFlow: FlowShape[GameMessageOut, JsValue] = builder.add(Flow[GameMessageOut].map { request =>
        request.json
      })
      playerActor ~> gameMessageToJsonFlow
      FlowShape(jsonToGameMessageFlow.in, gameMessageToJsonFlow.out)
  })
}
