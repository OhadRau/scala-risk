package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import javax.inject._

import scala.concurrent.Future
import scala.language.implicitConversions
import actors._
import play.api.libs.json.Json

class GameController @Inject()(cc: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  val logger = play.api.Logger(getClass)

  def ws = WebSocket.acceptOrResult[InEvent, OutEvent] { request =>
    Future.successful(request.session.get("user") match {
      case None => Left(Forbidden)
      case Some(_) => Right(ActorFlow.actorRef { out =>
        GameActor.props(out)
      })
    })
  }(Implicits.messageFlowTransformer)
}
