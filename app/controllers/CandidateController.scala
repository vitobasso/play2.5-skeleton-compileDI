package controllers

import models.{Candidate, Contact, Experience, Profile}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
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
      "availability" -> of(jodaLocalDateFormat("dd-MM-yyyy")),
      "expectedSalary" -> text,
      "ambition" -> text,
      "travel" -> text
    )(Profile.apply)(Profile.unapply),
    "contact" -> mapping(
      "email" -> nonEmptyText.verifying(hasEmailFormat),
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

  def hasEmailFormat: String => Boolean = _ matches "^[\\w\\d._%+-]+@[\\w\\d.-]+\\.[\\w]{2,}$"

  def get = Action.async {
    implicit request =>
      Future.successful(
        Ok(views.html.candidateForm(candidateForm))
      )
  }

  def post = Action.async {
    implicit request =>
      candidateForm.bindFromRequest().fold(
        formWithErrors => {
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

}
