import scala.language.implicitConversions

trait RepeatList[+T] extends Iterable[T]

case class Salary(employee: String, amount: Double)

case class RepeatListFromIterable[+T](iterable: Iterable[T]) extends RepeatList[T] {
  override def iterator: Iterator[T] = Iterator.continually(iterable).flatten.iterator
  override def flatMap[B](f: T => IterableOnce[B]): Iterable[B] = iterable.flatMap(f)
}

object RepeatList {
  def apply[T](iterable: Iterable[T]): RepeatList[T] = RepeatListFromIterable[T](iterable)
}

trait Multiply[M] {
  def twice(m: M): M
  def thrice(m: M): M
  def fourTimes(m: M): M
}

class MultiplySyntax[T](a: T) {
  def twice(implicit multiply: Multiply[T]): T = multiply.twice(a)
  def thrice(implicit multiply: Multiply[T]): T = multiply.thrice(a)
  def fourTimes(implicit multiply: Multiply[T]): T = multiply.fourTimes(a)
}

class MultiplyForSalary extends Multiply[Salary] {
  override def twice(m: Salary): Salary = m.copy(amount = m.amount * 2)
  override def thrice(m: Salary): Salary = m.copy(amount = m.amount * 3)
  override def fourTimes(m: Salary): Salary = m.copy(amount = m.amount * 4)
}

class MultiplyForRepeatList[T] extends Multiply[RepeatList[T]] {
  override def twice(m: RepeatList[T]): RepeatList[T] = RepeatList(m.flatMap(Seq.fill(2)(_)))
  override def thrice(m: RepeatList[T]): RepeatList[T] = RepeatList(m.flatMap(Seq.fill(3)(_)))
  override def fourTimes(m: RepeatList[T]): RepeatList[T] = RepeatList(m.flatMap(Seq.fill(4)(_)))
}

object `5` extends App {

  implicit def toMultiplySyntax[T: Multiply](value: T): MultiplySyntax[T] = new MultiplySyntax[T](value)

  implicit val multiplyForSalary: Multiply[Salary] = new MultiplyForSalary
  implicit val multiplyForRepeatListInt: Multiply[RepeatList[Number]] = new MultiplyForRepeatList[Number]
  implicit val multiplyForRepeatListString: Multiply[RepeatList[Any]] = new MultiplyForRepeatList[Any]

  val salary = Salary("Bob", 300.0)
  val list: RepeatList[Number] = RepeatList[Number](Seq(1, 2, 3.0))
  val list1: RepeatList[Any] = RepeatList[Any](Seq("value", 100, salary))

  println(list.twice.take(30).toList)
  println(list.thrice.take(30).toList)
  println(list.fourTimes.take(30).toList)

  println(list1.twice.take(10).toList)
  println(list1.thrice.take(10).toList)
  println(list1.fourTimes.take(12).toList)

  println(salary)
  println(salary.twice)
  println(salary.thrice)
  println(salary.fourTimes)
}
