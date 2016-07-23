package controllers

import models.Candidate
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection
import models.Candidate.candidateJsonFormat

class MyFirstController(val reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext)
  extends Controller with MongoController with ReactiveMongoComponents{

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("candidates"))

  val candidateForm = Form(
    mapping(
      "name" -> text,
      "email" -> optional(text),
      "skype" -> optional(text)
    )(Candidate.apply)(Candidate.unapply)
  )

  def get = Action.async {
    implicit request =>
    Future.successful(
      Ok(views.html.mongoform(candidateForm))
    )
  }

  def post = Action.async {
    implicit request =>
    candidateForm.bindFromRequest().fold(
      formWIthErrors => {
          Logger.error("form has errors")
          Future.successful(
            BadRequest(views.html.mongoform(candidateForm))
          )
      },
      candidate => {
        val futureResult = collection.flatMap(_.insert(candidate))
        futureResult.map(r=>Ok(r.message))
      }
    )
  }

}
