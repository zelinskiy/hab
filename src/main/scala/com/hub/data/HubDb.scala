package com.hub.data

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.dsl.{OneToMany, ManyToOne, CompositeKey2}
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp
import java.util.Date
import org.squeryl.KeyedEntity

object Article {
  def getOrdering(m: Option[String]) =
    (a1: Article, a2: Article) => m match {
      case Some("date") => a1.created.compareTo(a2.created) == 1
      case _ => a1.id.compareTo(a2.id) == 1
    }
}

case class Article(
  val id: Long,
  val boardId: Long,
  val userId: Long,
  val name: String,
  val body: String,
  val created: Date,
  val imgPath: Option[String])
    extends KeyedEntity[Long]{
  def this() = this(0,0,0,"","",new Date(0),None)
  lazy val board: ManyToOne[Board] =
    HubDb.articlesToBoards.right(this)
  lazy val user: ManyToOne[User] =
    HubDb.articlesToUsers.right(this)
}

case class Board(
  val id: Long,
  val categoryId: Long,
  val userId: Long,
  val name: String,
  val imgPath: Option[String])
    extends KeyedEntity[Long]{
  def this() = this(0,0,0,"",None)
  lazy val articles: OneToMany[Article] =
    HubDb.articlesToBoards.left(this)
  lazy val category: ManyToOne[Category] =
    HubDb.boardsToCategories.right(this)
  lazy val user: ManyToOne[User] =
    HubDb.boardsToUsers.right(this)
  lazy val subscribers =
    HubDb.boardsSubscriptions.right(this)
}

case class Category(
  val id: Long,
  val name: String)
    extends KeyedEntity[Long]{
  def this() = this(0,"")
  lazy val boards: OneToMany[Board] =
    HubDb.boardsToCategories.left(this)
}

case class User(
  val id: Long,
  val name: String,
  val about: String,
  val email: String,
  val passHash: String
)
    extends KeyedEntity[Long]{
  def this() = this(0,"username","nothing special",
    "username@mail.com","1a2b3c4d5e")
  lazy val articles: OneToMany[Article] =
    HubDb.articlesToUsers.left(this)
  lazy val boards: OneToMany[Board] =
    HubDb.boardsToUsers.left(this)
  lazy val subscriptions =
    HubDb.boardsSubscriptions.left(this)
}

case class Subscription(
  val userId: Long,
  val boardId: Long)
    extends KeyedEntity[CompositeKey2[Long,Long]]{
  def id = compositeKey(userId, boardId)
  def this() = this(0,0)
}
 
object HubDb extends Schema {
  val articles = table[Article]
  val boards = table[Board]
  val categories = table[Category]
  val users = table[User]

  on(articles)(a => declare(
    a.id is autoIncremented("articles_id_seq")))
  on(boards)(b => declare(
    b.id is autoIncremented("boards_id_seq")))
  on(categories)(c => declare(
    c.id is autoIncremented("categories_id_seq"))) 
  on(users)(u => declare(
    u.id is autoIncremented("users_id_seq"),
    u.name is unique,
    u.email is unique
  ))

  val articlesToBoards =
    oneToManyRelation(boards, articles).
      via((b,a) => b.id === a.boardId)

  val boardsToCategories =
    oneToManyRelation(categories, boards).
      via((c,b) => c.id === b.categoryId)

  val articlesToUsers =
    oneToManyRelation(users, articles).
      via((u,a) => u.id === a.userId)

  val boardsToUsers =
    oneToManyRelation(users, boards).
      via((u,b) => u.id === b.userId)

  val boardsSubscriptions =
    manyToManyRelation(users, boards).
      via[Subscription](
        (u, b, s) => (u.id === s.userId, b.id === s.boardId))
}
   

