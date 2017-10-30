package com.hub

import org.scalatra.auth.strategy.{BasicAuthStrategy, BasicAuthSupport}
import org.scalatra.auth.{ScentrySupport, ScentryConfig, ScentryStrategy}
import org.scalatra.{ScalatraBase}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.squeryl.PrimitiveTypeMode._
import scala.util.Try

import com.hub.data._

class OurBasicAuthStrategy(
  protected override val app: ScalatraBase,
  realm: String)
  extends ScentryStrategy[User]{

  def authenticate() (implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] =
    Try(HubDb.users
    .where(u => u.email === request.getParameter("email"))
    .where(u => MyUtils.sha256(request.getParameter("pass")) === u.passHash)
      .single).toOption
      
  
  protected def getUserId(user: User)(implicit request: HttpServletRequest, response: HttpServletResponse): String = user.id.toString
}

trait AuthenticationSupport extends ScentrySupport[User] {
  self: ScalatraBase =>

  val realm = "Scalatra Basic Auth Example"

  protected def fromSession = {
    case id: String =>
      HubDb.users.where(u => u.id.toString() === id).single
  }
  protected def toSession   = { case usr: User => usr.id.toString }

  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  override protected def configureScentry = {
    scentry.unauthenticated {
      scentry.strategies("Basic").unauthenticated()
    }
  }

  override protected def registerAuthStrategies = {
    scentry.register("Basic", app => new OurBasicAuthStrategy(app, realm))
  }

}
