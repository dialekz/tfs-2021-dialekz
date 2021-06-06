class Economy
class UpgradedEconomy extends Economy
class Special1b extends UpgradedEconomy
class ExtendedEconomy extends Economy
class Business extends ExtendedEconomy
class Elite extends Business
class Platinum extends Business

class ServiceLevelAdvance[T <: Economy] {
  def advance[TT <: T]: ServiceLevelAdvance[TT] = new ServiceLevelAdvance[TT]
}

object `2` extends App {
  val level1 = new ServiceLevelAdvance[Economy]
  val level2 = level1.advance[ExtendedEconomy]
  val level3 = level2.advance[Platinum]

  // ServiceLevelAdvance.advance` может только повышать уровень обслуживания
  //  val level4 = level3.advance[Economy]
}
