package models

import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.libs.json._

sealed trait Status
case object Applied extends Status
case object FirstInterview_Invited extends Status
sealed trait FirstInterview_Done extends Status
case object FirstInterview_Accepted extends FirstInterview_Done
case object FirstInterview_Rejected extends FirstInterview_Done
case object CodeReview_Invited extends Status
sealed trait CodeReview_Done extends Status
case object CodeReview_Accepted extends CodeReview_Done
case object CodeReview_Rejected extends CodeReview_Done
case object SecondInterview_Invited extends Status
sealed trait SecondInterview_Done extends Status
case object SecondInterview_Accepted extends SecondInterview_Done
case object SecondInterview_Rejected extends SecondInterview_Done
case object Hired extends Status
case object Withdrew extends Status

object Status {

  def fromString(string: String): Status =
    string
      .replaceAll("\"", "") // workaround reactivemongo json de-serializer giving strings with quotes
      match {
        case "Applied" => Applied
        case "FirstInterview_Invited" => FirstInterview_Invited
        case "FirstInterview_Accepted" => FirstInterview_Accepted
        case "FirstInterview_Rejected" => FirstInterview_Rejected
        case "CodeReview_Invited" => CodeReview_Invited
        case "CodeReview_Accepted" => CodeReview_Accepted
        case "CodeReview_Rejected" => CodeReview_Rejected
        case "SecondInterview_Invited" => SecondInterview_Invited
        case "SecondInterview_Accepted" => SecondInterview_Accepted
        case "SecondInterview_Rejected" => SecondInterview_Rejected
        case "Hired" => Hired
        case "Withdrew" => Withdrew
      }

  val values = List(Applied,
    FirstInterview_Invited, FirstInterview_Accepted, FirstInterview_Rejected,
    CodeReview_Invited, CodeReview_Accepted, CodeReview_Rejected,
    SecondInterview_Invited, SecondInterview_Accepted, SecondInterview_Rejected,
    Hired, Withdrew)

  implicit def statusFormat: Formatter[Status] = new Formatter[Status] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Status] =
      data.get(key) match {
        case Some(str) => Right(Status.fromString(str))
        case None => Left(Seq(FormError(key, "error.status")))
      }

    override def unbind(key: String, value: Status): Map[String, String] =
      Map(key -> value.toString)

  }

  implicit val jsonFormat: Format[Status] = Format(
    Reads(json => JsSuccess(Status.fromString(json.toString()))),
    Writes(status => JsString(status.toString)))

}