package controllers

import models.{Candidate, Contact, Experience, Profile}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.ReadPreference
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class CandidateController(val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi)
                         (implicit ec: ExecutionContext)
  extends Controller
    with MongoController
    with ReactiveMongoComponents
    with I18nSupport {

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("candidates"))

  val candidateForm: Form[Candidate] = Form(mapping(
    "profile" -> mapping(
      "name" -> nonEmptyText,
      "age" -> number,
      "country" -> text,
      "euWorker" -> boolean,
      "availability" -> text,
      "initialContact" -> date("dd-MM-yyyy"),
      "expectedSalary" -> text,
      "ambition" -> text,
      "travel" -> text
    )(Profile.apply)(Profile.unapply),
    "contact" -> mapping(
      "email" -> email,
      "skype" -> optional(text),
      "phone" -> optional(text),
      "codeRepo" -> optional(text)
    )(Contact.apply)(Contact.unapply),
    "experience" -> mapping(
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
      "soloWorking" -> text,
      "architectureAndDesign" -> text,
      "teamLeading" -> text
    )(Experience.apply)(Experience.unapply),
    "status" -> of[models.Status]
  )(Candidate.apply)(Candidate.unapply))

  def showForm = Action.async {
    implicit request =>
      Future.successful(
        Ok(views.html.candidateForm(candidateForm))
      )
  }

  def submitForm = Action.async {
    implicit request =>
      candidateForm.bindFromRequest().fold(
        formWithErrors => {
          Logger.error(formWithErrors.errors.toString)
          Future.successful(
            BadRequest(views.html.candidateForm(candidateForm))
          )
        },
        candidate => {
          val futureResult = collection.flatMap(_.insert(candidate))
          futureResult.map(r => Ok(r.message))
        }
      )
  }

  def list = Action.async {
    implicit request => {
      collection.flatMap { jsCollection =>
        val col: Future[Seq[Candidate]] = jsCollection.find(Json.obj())
          .cursor[Candidate](ReadPreference.primary)
          .collect[Seq]()
        col.map { c => Ok(views.html.candidateList(c)) }
      } recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

}
