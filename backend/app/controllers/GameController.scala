package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import javax.inject._

import scala.language.implicitConversions
import actors._

class GameController @Inject()(cc: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  val logger = play.api.Logger(getClass)

  import Implicits._

  def ws = WebSocket.accept[InEvent, OutEvent] { request =>
    ActorFlow.actorRef { out =>
        GameActor.props(out)
    }
  }
}
