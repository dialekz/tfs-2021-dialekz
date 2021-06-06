object solution {
  val gameOfThrones: GameOfThrones =
    new GameOfThrones(new Lannisters(75, 25), new Targaryen(50, 25))

  def main(args: Array[String]): Unit = {
    gameOfThrones
      .nextTurn(_.borrowMoney(), _.callDragon())
      .nextTurn(_.makeWildFire(), _.callDragon())
      .nextTurn(_.attack(_), _.attack(_))
      .nextTurn(_.makeWildFire(), _.attack(_))
      .nextTurn(_.attack(_), _.callDragon())
  }
}

case class Wealth(moneyAmount: Int, armyPower: Int) {
  override def toString: String = s"money: $moneyAmount, army: $armyPower"
}

trait GreatHouse {
  val name: String
  val wealth: Wealth
}

trait MakeWildFire {
  this: GreatHouse ⇒
  def makeWildFire(): Wealth = {
    val money: Int = this.wealth.moneyAmount
    val army: Int = this.wealth.armyPower + 50
    println(s"Great house of ${this.name} makes Wild Fire; money: $money, army: $army  ")
    Wealth(money, army)
  }
}

trait BorrowMoney {
  this: GreatHouse ⇒
  def borrowMoney(): Wealth = {
    val money: Int = this.wealth.moneyAmount + 50
    val army: Int = this.wealth.armyPower
    println(s"Great house of ${this.name} borrows money; money: $money, army: $army  ")
    Wealth(money, army)
  }
}

trait CallDragon {
  this: GreatHouse ⇒
  def callDragon(): Wealth = {
    val money: Int = this.wealth.moneyAmount
    val army: Int = this.wealth.armyPower * 2
    println(s"Great house of ${this.name} calls dragon; money: $money, army: $army  ")
    Wealth(money, army)
  }
}

trait Attack {
  this: GreatHouse ⇒
  def attack(aim: GreatHouse): (Wealth, Wealth) = {
    val attackArmy: Int = this.wealth.armyPower - aim.wealth.armyPower / 10
    val aimArmy: Int = aim.wealth.armyPower - this.wealth.armyPower / 5
    val attackWealth = Wealth(this.wealth.moneyAmount, if (attackArmy > 0) attackArmy else 0)
    val aimWealth = Wealth(aim.wealth.moneyAmount, if (aimArmy > 0) aimArmy else 0)
    println(s"Great house of ${this.name}($attackWealth) attacks ${aim.name}($aimWealth)  ")
    (attackWealth, aimWealth)
  }
}

case class Targaryen(wealth: Wealth) extends GreatHouse with BorrowMoney with CallDragon with Attack {
  override val name: String = "Targaryen"

  def this(money: Int, army: Int) = {
    this(Wealth(money, army))
  }
}

case class Lannisters(wealth: Wealth) extends GreatHouse with MakeWildFire with BorrowMoney with Attack  {
  override val name: String = "Lannisters"

  def this(money: Int, army: Int) = {
    this(Wealth(money, army))
  }
}

class GameOfThrones(val lannisters: Lannisters, val targaryen: Targaryen, val count: Int) {
  def this(lannisters: Lannisters, targaryen: Targaryen) = {
    this(lannisters, targaryen, 1)
  }

  def nextTurn(step1: Lannisters ⇒ Wealth, step2: Targaryen ⇒ Wealth): GameOfThrones = {
    println(s"Start turn $count in Game Of Thrones  ")
    new GameOfThrones(Lannisters(step1(lannisters)), Targaryen(step2(targaryen)), count + 1)
  }

  def nextTurn(step1: (Lannisters, Targaryen) ⇒ (Wealth, Wealth), step2: Targaryen ⇒ Wealth): GameOfThrones = {
    println(s"Start turn $count in Game Of Thrones  ")
    val (wealth1, wealth2) = step1(lannisters, targaryen)
    new GameOfThrones(Lannisters(wealth1), Targaryen(step2(Targaryen(wealth2))), count + 1)
  }

  def nextTurn(step1: Lannisters ⇒ Wealth, step2: (Targaryen, Lannisters) ⇒ (Wealth, Wealth)): GameOfThrones = {
    println(s"Start turn $count in Game Of Thrones  ")
    val (wealth1, wealth2) = step2(targaryen, Lannisters(step1(lannisters)))
    new GameOfThrones(Lannisters(wealth2), Targaryen(wealth1), count + 1)
  }

  def nextTurn(step1: (Lannisters, Targaryen) ⇒ (Wealth, Wealth), step2: (Targaryen, Lannisters) ⇒ (Wealth, Wealth)): GameOfThrones = {
    println(s"Start turn $count in Game Of Thrones  ")
    val (wealth1, wealth2) = step1(lannisters, targaryen)
    val (wealth3, wealth4) = step2(Targaryen(wealth2), Lannisters(wealth1))
    new GameOfThrones(Lannisters(wealth4), Targaryen(wealth3), count + 1)
  }
}
