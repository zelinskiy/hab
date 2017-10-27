package com.hub.app

import org.scalatra._
import org.squeryl.PrimitiveTypeMode._
import scala.util.{Try, Success, Failure}

import com.hub.data._
import com.hub.{MyUtils, AuthenticationSupport}

class MainServlet extends ScalatraServlet
    with DatabaseSessionSupport
    with AuthenticationSupport{

  def auth {
    if(!scentry.isAuthenticated) redirect("/auth/login")
  }

  get("/") {
    auth
    redirect("/boards")
  }

  get("/board/:id") {
    auth
    Try(HubDb.boards.where(b => b.id === params("id").toLong).single) match {
      case Success(b) => views.html.board(b)
      case Failure(e) => notFound(e)
    }
  }

  get("/boards") {
    auth
    views.html.boards(from (HubDb.boards) (select(_)))
  }

  


}
