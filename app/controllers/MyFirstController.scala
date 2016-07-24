package controllers

import models.{Candidate, Contact, Experience, Profile}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class MyFirstController(val reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext)
  extends Controller with MongoController with ReactiveMongoComponents {

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("candidates"))

  val candidateForm: Form[Candidate] = Form(mapping(
    "profile" -> mapping(
      "name" -> text,
      "age" -> number,
      "country" -> text,
      "euWorker" -> boolean,
      "availability" -> text,
      "expectedSalary" -> text
    )(Profile.apply)(Profile.unapply),
    "Contact" -> mapping(
      "email" -> text,
      "skype" -> optional(text),
      "phone" -> optional(text),
      "codeRepo" -> optional(text)
    )(Contact.apply)(Contact.unapply),
    "Experience" -> mapping(
      "csEducation" -> text,
      "commercial" -> text,
      "consulting" -> text,
      "java" -> text,
      "scala" -> text,
      "multipleLanguages" -> text,
      "functional" -> text,
      "designPatterns" -> text,
      "concurrency" -> text,
      "teamWorking" -> text,
      "soloWorking" -> text
    )(Experience.apply)(Experience.unapply),
    "status" -> of[models.Status]
  )(Candidate.apply)(Candidate.unapply))

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
          futureResult.map(r => Ok(r.message))
        }
      )
  }

}
