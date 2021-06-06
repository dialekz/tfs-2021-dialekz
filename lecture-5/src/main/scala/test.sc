trait RepeatList[+T] extends Iterable[T]
//
//object RepeatList {
//  def apply[T](iterable: Iterable[T]): RepeatList[T] = RepeatListFromIterable[T](iterable)
//}

case class RepeatListFromIterable[+T](iterable: Iterable[T]) extends RepeatList[T] {
  override def iterator: Iterator[T] = LazyList.continually(Seq.from(iterable)).flatten.iterator
//  private lazy val lazyImpl: Iterator[T] = LazyList.continually(Seq.from(iterable)).flatten.iterator
//  override def iterator = {
//    new Iterator[T] {
//      override def hasNext: Boolean = lazyImpl.hasNext
//      override def next: T = lazyImpl.next
//    }
//  }
//  override def knownSize: Int = if (iterable.isEmpty) 0 else -1
//  override def iterableFactory = LazyList
}

val itr: Iterable[Int] = Seq(1,2,3)
val a = LazyList.continually(itr).flatten.iterator
a.take(10).toList


//val b = RepeatListFromIterable(Seq(1,2,3))
RepeatListFromIterable(Seq(1,2,3)).take(10).toList

println("FINALLY")