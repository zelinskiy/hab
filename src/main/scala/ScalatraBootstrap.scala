import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext
import com.example.data.DatabaseInit

class ScalatraBootstrap extends LifeCycle
  with DatabaseInit {
  override def init(context: ServletContext) {
    configureDb()
    context.mount(new MyScalatraServlet, "/*")
  }

  override def destroy(context:ServletContext) {
    closeDbConnection()
  }
}
