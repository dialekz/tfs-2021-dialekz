import scala.annotation.tailrec

/**
 * Building should have:
 *  - string address
 *  - floors (link to first floor)
 *    Floor can be either residential floor or attic
 *    Each residential floor has two persons living on it and ladder to next floor (just a link)
 *    Attic has no person living in it
 *    Each person has age and sex (male/female)
 */
sealed trait Person {
  val age: Int
}

case class Man(age: Int) extends Person

case class Woman(age: Int) extends Person


case class Building(address: String, firstFloor: Floor)

sealed trait Floor

case class ResidentialFloor(person1: Person, person2: Person, nextFloor: Floor) extends Floor

case object Attic extends Floor

object Building {
  /**
   * Traverse building bottom to top applying function [[f]] on each residential floor accumulating
   * result in [[acc0]]
   */
  def protoFold(building: Building, acc0: Int)(f: (Int, ResidentialFloor) => Int): Int =
    goThroughFloor(building.firstFloor, acc0)(f)

  @tailrec
  def goThroughFloor(floor: Floor, acc0: Int)(f: (Int, ResidentialFloor) => Int): Int = floor match {
    case floor: ResidentialFloor ⇒ goThroughFloor(floor.nextFloor, f(acc0, floor))(f)
    case Attic ⇒ acc0
  }

  /**
   * Count number of floors where there is at least one man older than [[olderThan]]
   * NOTE: use [[protoFold]]
   */
  def countOldManFloors(building: Building, olderThan: Int): Int = {
    def isOldMan(person: Person): Boolean = person match {
      case man: Man if man.age > olderThan ⇒ true
      case _ ⇒ false
    }

    def foo(acc: Int, floor: ResidentialFloor): Int = floor match {
      case fl if isOldMan(fl.person1) || isOldMan(fl.person2) ⇒ acc + 1
      case _ ⇒ acc
    }

    protoFold(building, 0)(foo)
  }

  /**
   * Find age of eldest woman
   * NOTE: use [[protoFold]]
   */
  def womenMaxAge(building: Building): Int = {
    def isWoman(person: Person): Boolean = person match {
      case _: Woman ⇒ true
      case _ ⇒ false
    }

    def foo(acc: Int, floor: ResidentialFloor): Int = floor match {
      case fl if isWoman(fl.person1) && isWoman(fl.person2) ⇒ Math.max(acc, Math.max(fl.person1.age, fl.person2.age))
      case fl if isWoman(fl.person1) ⇒ Math.max(acc, fl.person1.age)
      case fl if isWoman(fl.person2) ⇒ Math.max(acc, fl.person2.age)
      case _ ⇒ acc
    }

    protoFold(building, 0)(foo)
  }

}

object BuildingMain {
  def main(args: Array[String]): Unit = {
    val floor3 = ResidentialFloor(Man(24), Man(26), Attic)
    val floor2 = ResidentialFloor(Man(32), Woman(56), floor3)
    val floor1 = ResidentialFloor(Woman(55), Woman(61), floor2)

    val building = Building("ул. Пушкина, д.Колотушкина", floor1)

    println(Building.countOldManFloors(building, 20)) // 2
    println(Building.countOldManFloors(building, 30)) // 1
    println(Building.countOldManFloors(building, 40)) // 0
    println()
    println(Building.womenMaxAge(Building("ул. Пушкина, д.Колотушкина", floor1))) // 61
    println(Building.womenMaxAge(Building("ул. Пушкина, д.Колотушкина", floor2))) // 56
    println(Building.womenMaxAge(Building("ул. Пушкина, д.Колотушкина", floor3))) // 0
  }
}
