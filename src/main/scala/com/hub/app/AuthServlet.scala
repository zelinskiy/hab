package com.hub.app

import org.scalatra._
import org.squeryl.PrimitiveTypeMode._
import scala.util.{Try, Success, Failure}

import com.hub.data._
import com.hub.{MyUtils, AuthenticationSupport}

class AuthServlet extends ScalatraServlet
    with DatabaseSessionSupport
    with AuthenticationSupport{

  get("/login"){
    views.html.login(None)
  }

  post("/login"){
    Try(HubDb.users
      .where(u => u.email === params("email"))
      .where(u => u.passHash
        === MyUtils.sha256(params("pass")))
      .single)
      match {
      case Success(u) => {
        scentry.authenticate("Basic")
        redirect("/")
      }
      case Failure(e) =>
        views.html.login(Some(e.getMessage))
    }
  }

  get("/register"){
    views.html.register(None)
  }  

  post("/register"){
    Try(HubDb.users.insert(
      new User(0,
        params("name"),
        params("email"),
        MyUtils.sha256(params("pass")))))
      match {
      case Success(u) => redirect("/")
      case Failure(e) =>
        views.html.register(Some(e.getMessage))
    }    
  }

  get("/logout"){
    scentry.logout
    views.html.login(None)
  }

  get("/create-db") {
    contentType = "text/html"
    HubDb.create
    redirect("/boards")
  }


}
