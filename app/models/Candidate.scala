package models

import play.api.libs.json.Json

case class Candidate(
                      profile: Profile,
                      contact: Contact,
                      experience: Experience,
                      status: Status
                    )

object Candidate {
  implicit val jsonFormat = Json.format[Candidate]
}