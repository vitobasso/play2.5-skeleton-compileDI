package models

import play.api.libs.json.Json

case class Experience(
                       csEducation: String,
                       commercial: String,
                       consulting: String,
                       java: String,
                       scala: String,
                       multipleLanguages: String,
                       functional: String,
                       designPatterns: String,
                       concurrency: String,
                       teamWorking: String,
                       soloWorking: String,
                       architectureAndDesign: String,
                       teamLeading: String
                     )

object Experience {
  implicit val jsonFormat = Json.format[Experience]
}