class KnowNothing
class Aggressive extends KnowNothing
class KnowSomething extends KnowNothing
class PoorlyEducated extends KnowSomething
class Normal extends PoorlyEducated
class Enlightened extends Normal
class Genius extends Enlightened

class SchoolClass[T <: KnowNothing](collection: Seq[T]) {
  def accept[TT >: T <: KnowNothing](students: Seq[TT]): SchoolClass[TT] = new SchoolClass[TT](collection ++ students)
}

object `3` extends App {
  //  val class0 = new SchoolClass[Genius](Seq(new KnowNothing)) // KnowNothing can't be in the class for Geniuses

  val class1: SchoolClass[Genius] = new SchoolClass[Genius](Seq(new Genius))
  val class2: SchoolClass[Enlightened] = class1.accept(Seq(new Enlightened))

  //  val normies: SchoolClass[Normal] = class2.accept(Seq(new Normal, new PoorlyEducated)) // To be PoorlyEducated is not enough

  val class3: SchoolClass[PoorlyEducated] = class2.accept(Seq(new Normal, new PoorlyEducated))
  val stillWithoutAggressive: SchoolClass[KnowSomething] = class3.accept(Seq(new Normal, new KnowSomething))
  val westBrooklyn: SchoolClass[KnowNothing] = stillWithoutAggressive.accept(Seq(new Aggressive, new KnowNothing))

  //  val noWayBack: SchoolClass[KnowSomething] = westBrooklyn.accept(Seq(new KnowSomething)) // Sorry, there is no any way back
}
