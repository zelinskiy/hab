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

  get("/") {
    redirect("/categories")
  }

  get("/article/:id") {
    scentry.authenticate("Basic")
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
          params("board"))
      case Success(b) => {
        Try(HubDb.articles.insert(
          new Article(0,
            b.id,
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
    scentry.authenticate("Basic")
    Try(HubDb.boards
      .where(b => b.id === params("id").toLong)
      .single)
      match {
      case Success(b) => views.html.board(b)
      case Failure(e) => notFound(e)
    }
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
          .insert(new Board(0, c.id, params("name"), None)))
          match {
          case Failure(e) => views.html.newBoard(Some(e.getMessage))
          case Success(b) => redirect("/board/" + c.id.toString)
        }
      }
    }
  }

  get("/category/:id") {
    scentry.authenticate("Basic")
    Try(HubDb.categories
      .where(c => c.id === params("id").toLong)
      .single)
      match {
      case Success(b) => views.html.category(b)
      case Failure(e) => notFound(e)
    }
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
