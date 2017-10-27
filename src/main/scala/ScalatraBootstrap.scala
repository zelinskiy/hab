import com.hub.app._
import org.scalatra._
import javax.servlet.ServletContext
import com.hub.data.DatabaseInit

class ScalatraBootstrap extends LifeCycle
  with DatabaseInit {
  override def init(context: ServletContext) {
    configureDb()    
    context.mount(new MainServlet, "/*")
    context.mount(new AuthServlet, "/auth/*")

    context.initParameters("org.scalatra.environment") = "development"
  }

  override def destroy(context:ServletContext) {
    closeDbConnection()
  }
}
