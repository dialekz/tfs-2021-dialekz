import scala.util.Random

trait Converter[-S] {
  def convert(value: S): String
}

trait Slide[+R] {
  def read: (Option[R], Slide[R])
}

// OOP15-UE: slide projector
class Projector[R](converter: Converter[R]) {
  def project(screen: Slide[R]): String = screen.read match {
    case (Some(line), nextScreen) => converter.convert(line) + project(nextScreen)
    case (None, _) => ""
  }
}

class WordLine(val word: String)

case class RedactedWordLine(redactionFactor: Double, override val word: String) extends WordLine(word)

object LineConverter extends Converter[WordLine] {
  override def convert(value: WordLine): String = value.word + "\n"
}

object RedactedWordLineConverter extends Converter[RedactedWordLine] {
  override def convert(value: RedactedWordLine): String = value.redactionFactor match {
    case x if x * 100 > Random.nextInt(100) => value.word.map(_ => '█') + "\n"
    case _ => value.word + "\n"
  }
}

case class HelloSlide[R <: WordLine](lines: Seq[R]) extends Slide[R] {
  override def read: (Option[R], Slide[R]) = lines match {
    case head +: tail => (Option(head), HelloSlide(tail))
    case Nil => (None, HelloSlide(Nil))
  }
}

object `4` extends App {
  val redactedWordLineSlides: HelloSlide[RedactedWordLine] = HelloSlide(
    Seq(RedactedWordLine(1.0, "masked"), RedactedWordLine(0.0, "non masked"), RedactedWordLine(0.5, "maybe masked")))
  val wordLineSlides: HelloSlide[WordLine] = HelloSlide(
    Seq(RedactedWordLine(1.0, "masked"), RedactedWordLine(0.0, "non-masked"), new WordLine("regular world line")))

  val redactedWordLineProjector = new Projector[RedactedWordLine](RedactedWordLineConverter)
  val redactedWordLineProjectorWithWordLineConverter = new Projector[RedactedWordLine](LineConverter)
  val wordLineProjector = new Projector[WordLine](LineConverter)

  // В проекторе для WordLine можно использовать Converter[WordLine], но нельзя Converter[RedactedWordLine]
  //  val wordLineProjectorWithRedactedWordLineProjector = new Projector[WordLine](RedactedWordLineConverter)

  println("RedactedWordLineProjector shows RedactedWordLine slides:\n" +
    redactedWordLineProjector.project(redactedWordLineSlides))
  println("RedactedWordLineProjector with WordLineConverter shows RedactedWordLine slides:\n" +
    redactedWordLineProjectorWithWordLineConverter.project(redactedWordLineSlides))
  println("WordLineProjector shows RedactedWordLine slides:\n" +
    wordLineProjector.project(redactedWordLineSlides))

  // В проекторе для RedactedWordLine можно проецировать Slide[RedactedWordLine], но нельзя Slide[WordLine]
  //  println(redactedWordLineProjector.project(wordLineSlides))
  //  println(redactedWordLineProjectorWithWordLineConverter.project(wordLineSlides))

  println("WordLineProjector shows WordLine slides:\n" +
    wordLineProjector.project(wordLineSlides))
}
