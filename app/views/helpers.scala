package views

import play.api.data.Form
import play.api.i18n.Messages
import views.html.b3.B3FieldConstructor

/**
  * Use field id as label to avoid duplication
  */
object helpers {

  def text[T](form: Form[T], fieldId: String)
             (implicit messages: Messages, fc: B3FieldConstructor) =
    html.b3.text(form(fieldId), '_label -> Messages(fieldId))

  def checkbox[T](form: Form[T], fieldId: String)
                 (implicit messages: Messages, fc: B3FieldConstructor) =
    html.b3.checkbox(form(fieldId), '_label -> Messages(fieldId))

  def select[T](form: Form[T], fieldId: String, options: Seq[String])
               (implicit messages: Messages, fc: B3FieldConstructor) = {
    def idAndLabel(optionId: String) = (optionId, Messages(s"$fieldId.$optionId"))
    html.b3.select(form(fieldId), options map idAndLabel)
  }

  /**
    * Workaround play templates not allowing type param
    */
  def table[T](items: Seq[T])(headers: Seq[String], toRow: T => Seq[String])
              (implicit messages: Messages) =
    html.table(headers, items.map(toRow(_)))

}
