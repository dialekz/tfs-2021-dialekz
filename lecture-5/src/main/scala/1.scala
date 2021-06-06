sealed trait Language
case object Russian extends Language
case object English extends Language

trait Greeting[T <: Language] {
  def text: String
}

case object Privet extends Greeting[Russian.type] {
  override val text: String = "Привет"
}
case object Zdorovo extends Greeting[Russian.type] {
  override val text: String = "Здорово!"
}
case object Hello extends Greeting[English.type] {
  override val text: String = "Hello"
}

class Greeter[T <: Language] {
  def greet(greetings: Greeting[T]): Unit = println(greetings.text)
}

object `1` extends App {
  val rusGreeter = new Greeter[Russian.type]
  rusGreeter.greet(Privet)
  rusGreeter.greet(Zdorovo)

  // Поприветствовать на чужом языке не получится
  //  rusGreeter.greet(Hello)
}
