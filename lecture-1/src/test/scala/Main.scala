import scala.io.StdIn
object Main extends App {
  val x = 5
  val y = {
    val x = 7
    x + 3
  }
  println(x + "," + y)
}

object Config {
  val name = "Hello, "
}