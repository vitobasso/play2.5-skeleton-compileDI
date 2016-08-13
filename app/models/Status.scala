package models

import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.libs.json._

sealed trait Status{
  def toDisplayString: String = Status.displayMap(this)
}
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

  private val displayMap = Map[Status, String] (
    Applied -> "applied",
    FirstInterview_Invited -> "firstInterview.invited",
    FirstInterview_Accepted -> "firstInterview.accepted",
    FirstInterview_Rejected -> "firstInterview.rejected",
    CodeReview_Invited -> "codeReview.invited",
    CodeReview_Accepted -> "codeReview.accepted",
    CodeReview_Rejected -> "codeReview.rejected",
    SecondInterview_Invited -> "secondInterview.invited",
    SecondInterview_Accepted -> "secondInterview.accepted",
    SecondInterview_Rejected -> "secondInterview.rejected",
    Hired -> "hired",
    Withdrew -> "withdrew"
  )

  def fromDisplayString(string: String): Status = {
    val cleanString = string.replaceAll("\"", "") // workaround: reactivemongo json de-serializer giving strings with quotes
    val inverseMap = displayMap.map(_.swap)
    inverseMap(cleanString)
  }

  val values = List(
    Applied,
    FirstInterview_Invited, FirstInterview_Accepted, FirstInterview_Rejected,
    CodeReview_Invited, CodeReview_Accepted, CodeReview_Rejected,
    SecondInterview_Invited, SecondInterview_Accepted, SecondInterview_Rejected,
    Hired, Withdrew
  )

  val displayValues = values.map(_.toDisplayString)

  implicit def statusFormat: Formatter[Status] = new Formatter[Status] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Status] =
      data.get(key) match {
        case Some(str) => Right(Status.fromDisplayString(str))
        case None => Left(Seq(FormError(key, "error.status")))
      }

    override def unbind(key: String, value: Status): Map[String, String] =
      Map(key -> value.toString)

  }

  implicit val jsonFormat: Format[Status] = Format(
    Reads(json => JsSuccess(Status.fromDisplayString(json.toString()))),
    Writes(status => JsString(status.toDisplayString)))

}