package models

import play.api.libs.json.Json

case class Contact(
                    email: String,
                    skype: Option[String],
                    phone: Option[String],
                    codeRepo: Option[String]
                  )

object Contact {
  implicit val jsonFormat = Json.format[Contact]
}