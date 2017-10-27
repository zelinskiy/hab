package com.hub.data

import com.mchange.v2.c3p0.ComboPooledDataSource
import org.squeryl.adapters.{H2Adapter, MySQLAdapter}
import org.squeryl.Session
import org.squeryl.SessionFactory

trait DatabaseInit {
  val databaseUsername = "root"
  val databasePassword = ""
  val databaseConnection = "jdbc:mysql://localhost:3306/test2"

  var cpds = new ComboPooledDataSource

  def configureDb() {
    cpds.setDriverClass("com.mysql.jdbc.Driver")
    cpds.setJdbcUrl(databaseConnection)
    cpds.setUser(databaseUsername)
    cpds.setPassword(databasePassword)

    cpds.setMinPoolSize(1)
    cpds.setAcquireIncrement(1)
    cpds.setMaxPoolSize(50)

    SessionFactory.concreteFactory = Some(() => connection)

    def connection = {
      Session.create(cpds.getConnection, new MySQLAdapter)
    }
  }

  def closeDbConnection() {
    cpds.close()
  }
}
