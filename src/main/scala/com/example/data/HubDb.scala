package com.example.data

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp

class Board(
  val id: Long,
  val title: String,
  val body: String){

  def this() = this(0,"","")
}
 
object HubDb extends Schema { 
  val boards = table[Board]
}
   
