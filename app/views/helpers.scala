package views

import play.api.data.Form
import play.api.i18n.Messages
import views.html.b4.B4FieldConstructor

/**
  * Use field id as label to avoid duplication
  */
object helpers {

  def text[T](form: Form[T], fieldId: String)
             (implicit messages: Messages, fc: B4FieldConstructor) =
    html.b4.text(form(fieldId), '_label -> Messages(fieldId))

  def checkbox[T](form: Form[T], fieldId: String)
             (implicit messages: Messages, fc: B4FieldConstructor) =
    html.b4.checkbox(form(fieldId), '_label -> Messages(fieldId))

  def select[T](form: Form[T], fieldId: String, options: Seq[String])
             (implicit messages: Messages, fc: B4FieldConstructor) = {
    def idAndLabel(optionId: String) = (optionId, Messages(s"$fieldId.$optionId"))
    html.b4.select(form(fieldId), options map idAndLabel)
  }

}
