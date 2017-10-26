package com.example.data

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp
import org.squeryl.KeyedEntity

class Board(
  val id: Long,
  val name: String,
  val description: Option[String])
    extends KeyedEntity[Long]{

  def this() = this(0,"",None)
}
 
object HubDb extends Schema { 
  val boards = table[Board]

  on(boards)(b => declare(
    b.id is autoIncremented("boards_id_seq")))
}
   

