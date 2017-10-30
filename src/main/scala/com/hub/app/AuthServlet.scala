package com.hub.app

import org.scalatra._
import org.squeryl.PrimitiveTypeMode._
import scala.util.{Try, Success, Failure}
import org.squeryl.Query

import com.hub.data._
import com.hub.{MyUtils, AuthenticationSupport}

class AuthServlet extends ScalatraServlet
    with DatabaseSessionSupport
    with AuthenticationSupport{

  implicit val cats: Query[Category] = HubDb.categories

  get("/login"){
    views.html.login(None)
  }

  get("/logged"){
    scentry.isAuthenticated
  }

  post("/login"){
    scentry.authenticate("Basic") match {
      case Some(u) => {
        scentry.authenticate("Basic")
        redirect("/")
      }
      case None =>
        views.html.login(Some("Cant login"))
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
      case Success(u) => {
        redirect("/auth/login")
      }
      case Failure(e) =>
        views.html.register(Some(e.getMessage))
    }    
  }

  get("/logout"){
    scentry.logout
    redirect("/auth/login")
  }

}
