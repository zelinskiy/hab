package com.hub.app

import org.scalatra._
import org.squeryl.PrimitiveTypeMode._
import scala.util.{Try, Success, Failure}

import com.hub.data._
import com.hub.{MyUtils, AuthenticationSupport}

class MainServlet extends ScalatraServlet
    with DatabaseSessionSupport
    with AuthenticationSupport{

  get("/") {
    redirect("/categories")
  }

  get("/article/:id") {
    scentry.authenticate("Basic")
    views.html.hello()
  }

  get("/board/:id") {
    scentry.authenticate("Basic")
    Try(HubDb.boards.where(b => b.id === params("id").toLong).single) match {
      case Success(b) => views.html.board(b)
      case Failure(e) => notFound(e)
    }
  }

  get("/category/:id") {
    scentry.authenticate("Basic")
    views.html.hello()
  }

  get("/categories") {
    views.html.categories(
      from (HubDb.categories) (select(_))
    )
  }

  get("/create-db") {
    contentType = "text/html"
    HubDb.create
    redirect("/")
  }

}
