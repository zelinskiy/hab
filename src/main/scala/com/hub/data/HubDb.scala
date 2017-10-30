package com.hub.data

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.dsl.{OneToMany, ManyToOne}
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp
import java.util.Date
import org.squeryl.KeyedEntity

case class Article(
  val id: Long,
  val boardId: Long,
  val name: String,
  val body: String,
  val created: Date,
  val imgPath: Option[String])
    extends KeyedEntity[Long]{
  def this() = this(0,0,"","",new Date(0),None)
  lazy val board: ManyToOne[Board] = HubDb.articlesToBoards.right(this)
}

case class Board(
  val id: Long,
  val categoryId: Long,
  val name: String,
  val imgPath: Option[String])
    extends KeyedEntity[Long]{
  def this() = this(0,0,"",None)
  lazy val articles: OneToMany[Article] = HubDb.articlesToBoards.left(this)
  lazy val category: ManyToOne[Category] = HubDb.boardsToCategories.right(this)
}

case class Category(
  val id: Long,
  val name: String)
    extends KeyedEntity[Long]{
  def this() = this(0,"")
  lazy val boards: OneToMany[Board] = HubDb.boardsToCategories.left(this)
}

case class User(
  val id: Long,
  val name: String,
  val email: String,
  val passHash: String
)
    extends KeyedEntity[Long]{
  def this() = this(0,"username",
    "username@mail.com","1a2b3c4d5e")
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

  val articlesToBoards =
    oneToManyRelation(boards, articles).
      via((b,a) => b.id === a.boardId)

  val boardsToCategories =
    oneToManyRelation(categories, boards).
      via((c,b) => c.id === b.categoryId)

  on(users)(u => declare(
    u.id is autoIncremented("users_id_seq"),
    u.name is unique,
    u.email is unique
  ))
}
   

