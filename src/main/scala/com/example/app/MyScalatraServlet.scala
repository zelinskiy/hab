package com.example.app

import org.scalatra._
import org.squeryl.PrimitiveTypeMode._
import scala.util.{Try, Success, Failure}

import com.example.data._
import com.example.{MyUtils, AuthenticationSupport}

class MyScalatraServlet extends ScalatraServlet
    with DatabaseSessionSupport
    with AuthenticationSupport{

  get("/") {
    if(scentry.isAuthenticated)
      redirect("/boards")
    else redirect("/login")
  }

  get("/try-auth") {
    scentry.isAuthenticated
  }

  get("/login"){
    views.html.login("None")
  }

  post("/login"){
    Try(HubDb.users
      .where(u => u.email === params("email"))
      .where(u => u.passHash === MyUtils.sha256(params("pass")))
      .single)
      match {
      case Success(u) => {
        scentry.authenticate("Basic")
        redirect("/")
      }
      case Failure(e) => views.html.login(e.getMessage)
    }
  }

  get("/register"){
    views.html.register("None")
  }  

  post("/register"){
    Try(HubDb.users.insert(
      new User(0,
        params("name"),
        params("email"),
        MyUtils.sha256(params("pass")))))
      match {
      case Success(u) => redirect("/")
      case Failure(e) => views.html.register(e.getMessage)
    }    
  }

  get("/logout"){
    scentry.logout
    views.html.login("None")
  }

  get("/board/:id") {
    Try(HubDb.boards.where(b => b.id === params("id").toLong).single) match {
      case Success(b) => views.html.board(b)
      case Failure(e) => NotFound(e.getMessage)
    }
  }

  get("/boards"){
    views.html.boards(from (HubDb.boards) (select(_)))
  }

  get("/create-db") {
    contentType = "text/html"
    HubDb.create
    redirect("/boards")
  }


}
