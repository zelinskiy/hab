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

  get("/article/my"){
    views.html.articles(
      from (HubDb.articles)(a =>
        where(a.userId === scentry.user.id)
        select(a)))
  }

  get("/board/:id") {
    val o = Article.getOrdering(params.get("order"))
    Try(HubDb.boards
      .where(b => b.id === params("id").toLong)
      .single)
      match {
      case Success(b) =>
        views.html.board(o, b)
      case Failure(e) => notFound(e)
    }
  }

  get("/board/:id/sub") {
    Try(HubDb.boardsSubscriptions
      .where(s => s.boardId === params("id").toLong
      and s.userId === scentry.user.id)
      .single)
      match {
      case Success(s) => {
        HubDb.boardsSubscriptions.delete(s.id)
        redirect("/board/subscriptions")
      }
      case Failure(e) =>
        Try(HubDb.boardsSubscriptions
          .insert(new Subscription(
            scentry.user.id,
            params("id").toLong)))
          match {
            case Success(s) =>
              redirect("/board/subscriptions")
            case Failure(e) =>
              notFound(e)
          }
    }
  }

  get("/board/:id/subscribed") {
    Try(HubDb.boardsSubscriptions
      .where(s => s.userId === scentry.user.id
        and s.boardId === params("id").toLong)
    ).isSuccess
  }

  get("/board/subscriptions"){
    views.html.boards(
      "Subscriptions",
      from(HubDb.boards)(b =>
        where(scentry.user.id in b.subscribers.map(_.id))
        select(b)))      
  }

  get("/board/my"){
    views.html.boards(
      "My Boards",
      from (HubDb.boards)(b =>
        where(b.userId === scentry.user.id)
        select(b)))
  }

  get("/board/new/:cid"){
    views.html.newBoard(None, params("cid"))
  }

  post("/board/new/:cid"){
    Try(HubDb.categories
      .where(c => c.id === params("cid").toLong)
      .single)
      match {
      case Failure(_) =>
        views.html.newBoard(
          Some("Can't find category " + params("category")),
          params("cid"))
      case Success(c) => {
        Try(HubDb.boards
          .insert(
            new Board(0,
              c.id,
              scentry.user.id,
              params("name"),
              new Date,
              None)))
          match {
          case Failure(e) => views.html.newBoard(
            Some(e.getMessage), params("cid"))
          case Success(b) => redirect("/board/" + b.id.toString)
        }
      }
    }
  }

  get("/category/:id") {
    val o = Board.getOrdering(params.get("order"))
    Try(HubDb.categories
      .where(c => c.id === params("id").toLong)
      .single)
      match {
      case Success(c) => views.html.category(o, c)
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

  get("/overview"){
    views.html.overview()
  }  
}
