package com.hub.app

import org.scalatra._
import org.squeryl.Query
import org.squeryl.PrimitiveTypeMode._
import scala.util.{Try, Success, Failure}
import java.util.Date

import com.hub.data._
import com.hub.{MyUtils, AuthenticationSupport}

class MainServlet extends ScalatraServlet
    with DatabaseSessionSupport
    with AuthenticationSupport{

  implicit val cats: Query[Category] = HubDb.categories

  def auth = {
    if(!scentry.isAuthenticated) redirect("/auth/login")
  }

  before(){
    auth
  }

  get("/") {
    redirect("/categories")
  }

  get("/article/:id") {
    Try(HubDb.articles
      .where(a => a.id === params("id").toLong)
      .single)
      match {
      case Success(a) => views.html.article(a)
      case Failure(e) => notFound(e)
    }
  }

  get("/article/new/:bid"){
    views.html.newArticle(None, params("bid"))
  }

  post("/article/new/:bid"){
    Try(HubDb.boards
      .where(b => b.id === params("bid").toLong)
      .single)
      match {
      case Failure(_) =>
        views.html.newArticle(
          Some("Can't find board " + params("bid")),
          params("bid"))
      case Success(b) => {
        Try(HubDb.articles.insert(
          new Article(0,
            b.id,
            scentry.user.id,
            params("title"),
            params("body"),
            new Date,
            None)))
          match {
          case Failure(e) =>
            views.html.newArticle(
              Some(e.getMessage), params("bid"))
          case Success(a) => redirect("/article/" + a.id.toString)
        }
      }
    }
  }


  get("/board/:id") {
    Try(HubDb.boards
      .where(b => b.id === params("id").toLong)
      .single)
      match {
      case Success(b) => views.html.board(b)
      case Failure(e) => notFound(e)
    }
  }

  get("/board/my"){
    views.html.myBoards(
      from (HubDb.boards)(b =>
        where(b.id === scentry.user.id)
        select(b)))
  }

  get("/board/new"){
    views.html.newBoard(None)
  }

  post("/board/new"){
    Try(HubDb.categories
      .where(c => c.name === params("category"))
      .single)
      match {
      case Failure(_) =>
        views.html.newBoard(
          Some("Can't find category " + params("category")))
      case Success(c) => {
        Try(HubDb.boards
          .insert(
            new Board(0,
              c.id,
              scentry.user.id,
              params("name"),
              None)))
          match {
          case Failure(e) => views.html.newBoard(Some(e.getMessage))
          case Success(b) => redirect("/board/" + b.id.toString)
        }
      }
    }
  }

  get("/category/:id") {
    Try(HubDb.categories
      .where(c => c.id === params("id").toLong)
      .single)
      match {
      case Success(c) => views.html.category(c)
      case Failure(e) => notFound(e)
    }
  }

  get("/categories") {
    views.html.categories(
      from (HubDb.categories) (select(_))
    )
  }

  get("/me"){
    views.html.profile(scentry.user)
  }

  
}
