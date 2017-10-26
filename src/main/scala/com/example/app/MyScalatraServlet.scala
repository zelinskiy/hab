package com.example.app

import org.scalatra._
import org.squeryl.PrimitiveTypeMode._

import com.example.data._

class MyScalatraServlet extends ScalatraServlet
  with DatabaseSessionSupport{

  get("/") {
    views.html.hello()
  }

  get("/board/:id") {
    views.html.board(params("id"))
  }

  get("/boards"){
    views.html.boards(from(HubDb.boards)(select(_)))
  }

  get("/create-db") {
    contentType = "text/html"

    HubDb.create
    redirect("/boards")
  }


}
