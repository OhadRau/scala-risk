package actors

import akka.actor.{Actor, Props}

import scala.collection.mutable

object ChatActor {
  def props(clients: mutable.HashMap[String, ClientWithActor]) = Props(new ChatActor(clients))
}

class ChatActor(clients: mutable.HashMap[String, ClientWithActor]) extends Actor {
  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    // TODO: Not O(n) search
    case MessageToUser(token, publicToken, message: String) =>
      clients.get(token) match {
        case Some(client) =>
          clients.find(_._2.client.publicToken == publicToken) match {
            case Some((_, recipient)) =>
              recipient.actor ! UserMessage(client.client.name getOrElse "", message)
              client.actor ! UserMessage(client.client.name getOrElse "", message)
            case None => sender() ! Err("Invalid recipient.")
          }
        case None => sender() ! Err("Invalid Token.")
      }
  }
}
