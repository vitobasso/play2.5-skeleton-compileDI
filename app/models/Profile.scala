package models

import play.api.libs.json.Json

case class Profile(
                    name: String,
                    age: Int,
                    country: String,
                    euWorker: Boolean,
                    availability: String,
                    expectedSalary: String
                  )

object Profile {
  implicit val profileJsonFormat = Json.format[Profile]
}