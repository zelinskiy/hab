package com.hub.data

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp
import org.squeryl.KeyedEntity

case class Board(
  val id: Long,
  val name: String,
  val description: Option[String])
    extends KeyedEntity[Long]{
  def this() = this(0,"",None)
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
  val boards = table[Board]
  val users = table[User]

  on(boards)(b => declare(
    b.id is autoIncremented("boards_id_seq")))
  on(users)(u => declare(
    u.id is autoIncremented("users_id_seq"),
    columns (u.name, u.email) are unique
  ))
}
   

