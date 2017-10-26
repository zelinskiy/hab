package com.example.app

import org.scalatra._
import org.squeryl.PrimitiveTypeMode._
import scala.util.Try

import com.example.data._

class MyScalatraServlet extends ScalatraServlet
  with DatabaseSessionSupport{

  get("/") {
    views.html.hello()
  }

  get("/board/:id") {
    Try(params("id").toLong).toOption match {
      case Some(id) => {
        val b = HubDb.boards.where(b => b.id === id).single
        views.html.board(b)
      }
      case None => notFound(_)
    }
  }

  get("/boards"){
    views.html.boards(from(HubDb.boards)(select(_)))
  }

  get("/create-db") {
    contentType = "text/html"
    HubDb.drop
    HubDb.create
    redirect("/boards")
  }


}
