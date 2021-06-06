package ru.tinkoff.lecture7

object Program_04 extends App {
  val compressor = new Compressor_03

  val input = Seq(1, 2, 2, 3, 3, 3, 4)
  val output = Seq((1, 1), (2, 2), (3, 3), (4, 1))
  println(compressor.compress(input))
  assert(compressor.compress(input) == output)
}

class Compressor_04 {
  def compress(numbers: Seq[Int]): Seq[(Int, Int)] = {
    numbers
      .foldLeft(List[(Int, Int)]()) { // <-- нет мутабельного состояния
        case (acc@(last -> counter) :: tail, elem) =>
          if (elem == last) (elem -> (counter + 1)) :: tail
          else (elem -> 1) :: acc
        case (Nil, elem) => (elem -> 1) :: Nil
      }
      .reverse // <-- тут получаем копирование
  }
}