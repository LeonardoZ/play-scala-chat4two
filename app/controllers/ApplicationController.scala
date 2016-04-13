package controllers

import javax.inject.{Inject, Singleton}

import play.api._
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc._


@Singleton
class ApplicationController @Inject extends Controller {

  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  def index =  Action { implicit req =>
    Ok(views.html.index(routes.ApplicationController.chatFeed(), routes.ApplicationController.postMessage()))
  }

  def chatFeed = Action { req =>
    println("Someone connected :" + req.remoteAddress )
    Ok.chunked(chatOut &> EventSource()).as("text/event-stream")
  }

  def postMessage = Action(parse.json) { req => chatChannel.push(req.body); Ok}


}
