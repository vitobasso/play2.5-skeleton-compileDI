import controllers._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.cache.EhCacheComponents
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.modules.reactivemongo.DefaultReactiveMongoApi
import router.Routes

import scala.concurrent.ExecutionContext

class AppLoader extends ApplicationLoader {

  implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  override def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }

    val components: AppComponents = new AppComponents(context)
    components.application
  }

}

class AppComponents(context: Context)(implicit val ec: ExecutionContext)
  extends BuiltInComponentsFromContext(context) with EhCacheComponents {

  val config = configuration.underlying
  implicit val scheduler = actorSystem.scheduler

  lazy val langs: DefaultLangs = new DefaultLangs(configuration)
  implicit lazy val messageApi = new DefaultMessagesApi(environment, configuration, langs)
  implicit lazy val reactiveMongoApi = new DefaultReactiveMongoApi(context.initialConfiguration, applicationLifecycle)

  lazy val homeController = new HomeController
  lazy val countController = new CountController(services.AtomicCounter)
  lazy val asyncController = new AsyncController(actorSystem)
  lazy val myFirstController = new MyFirstController
  lazy val assetsController: Assets = new Assets(httpErrorHandler)

  // order matters - should be the same as routes file
  lazy val router = new Routes(
    httpErrorHandler,
    homeController,
    countController,
    asyncController,
    myFirstController,
    assetsController)

}

