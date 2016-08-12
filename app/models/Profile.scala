package models

import org.joda.time.LocalDate
import play.api.libs.json.Json

case class Profile(
                    name: String,
                    age: Int,
                    country: String,
                    euWorker: Boolean,
                    availability: String,
                    initialContact: LocalDate,
                    expectedSalary: String,
                    ambition: String,
                    travel: String
                  )

object Profile {
  implicit val jsonFormat = Json.format[Profile]
}