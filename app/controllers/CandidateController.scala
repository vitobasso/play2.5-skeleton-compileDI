package controllers

import java.io.File

import models.{Candidate, Contact, Experience, Profile}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import service.CandidateService
import views.html

import scala.concurrent.{ExecutionContext, Future}

class CandidateController(val messagesApi: MessagesApi, val service: CandidateService)(implicit ec: ExecutionContext)
  extends Controller
    with I18nSupport {

  val form: Form[Candidate] = Form(mapping(
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
        Ok(html.candidateForm(form))
      )
  }

  def submitForm = Action.async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {
          Logger.error(formWithErrors.errors.toString)
          Future.successful(
            BadRequest(html.candidateForm(formWithErrors))
          )
        },
        candidate =>
          service.insert(candidate).map(r =>
            Redirect(controllers.routes.CandidateController.list())
          )
      )
  }

  def list = Action.async {
    implicit request =>
      for (candidates <- service.list)
        yield Ok(html.candidateList(candidates))
  }

  def edit(candidateId: Long) = Action.async {
    implicit request =>
      service.find(candidateId).map {
        case None => BadRequest(s"Candidate not found with id=$candidateId")
        case Some(candidate) =>
          Ok(html.candidateForm(form.fill(candidate)))
      }
  }

  def uploadPage = Action.async {
    implicit request =>
      Future.successful(
        Ok(views.html.upload(form))
      )
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { file =>
      file.ref.moveTo(new File(s"upload/${file.filename}"))
      Ok("File uploaded")
    }.getOrElse {
      BadRequest("Missing file")
    }
  }

}
