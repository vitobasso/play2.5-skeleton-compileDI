import controllers.{Assets, AsyncController, CountController, HomeController}
import play.api.ApplicationLoader.Context
import play.api.cache.EhCacheComponents
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, Configuration, LoggerConfigurator}

import scala.concurrent.ExecutionContext
import router.Routes

@SuppressWarnings(Array(
  // This is the 'end of the world' (or more the beginning), if anything fails here, the app crashes immediately, which is what we want to happen.
  "org.danielnixon.playwarts.GenMapLikePartial",
  // This is the 'end of the world', exclusively here it's fine to get a reference to the Play Global execution context.
  "org.danielnixon.playwarts.PlayGlobalExecutionContext"))
class AppLoader extends ApplicationLoader {

  implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  override def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }



    val components: AppComponents = new AppComponents(context)
    //    if (context.initialConfiguration.underlying.getBoolean("discovery.enabled")) {
    //     do sthg
    //    }
    //    if (context.initialConfiguration.underlying.getBoolean("XXX")) {
    //      start service
    //      components.applicationLifecycle.addStopHook { () =>
    //        Future(stop service)
    //      }
    //    }
    components.application
  }

}

@SuppressWarnings(Array(
  // This is the 'end of the world' (or more the beginning), if anything fails here, the app crashes immediately, which is what we want to happen.
  "org.danielnixon.playwarts.GenMapLikePartial",
  "org.brianmckenna.wartremover.warts.Throw"))
class AppComponents(context: Context)(implicit val ec: ExecutionContext) extends BuiltInComponentsFromContext(context)
  with EhCacheComponents {

  val config = context.initialConfiguration.underlying

  implicit val scheduler = actorSystem.scheduler



  lazy val homeController = new HomeController
  lazy val countController = new CountController(services.AtomicCounter)
  lazy val asyncController = new AsyncController(actorSystem)

  lazy val assetsController: Assets = new Assets(httpErrorHandler)

  // order matters - should be the same as routes file
  lazy val router = new Routes(
    httpErrorHandler,
    homeController,
    countController,
    asyncController,
    assetsController)

}

