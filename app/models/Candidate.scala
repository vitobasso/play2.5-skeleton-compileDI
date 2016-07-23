package models

import play.api.libs.json.Json

case class Candidate (
                name: String,
                email: Option[String],
                skype: Option[String]
                )

object Candidate {
  implicit val candidateJsonFormat = Json.format[Candidate]
}