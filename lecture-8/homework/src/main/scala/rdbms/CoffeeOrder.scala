package rdbms

import rdbms.CustomersQueryRepository.AllCustomers
import slick.dbio.Effect
import slick.jdbc.H2Profile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep}

import scala.concurrent.ExecutionContext


case class CoffeeOrder(id: Int,
                       name: String,
                       price: BigDecimal,
                       nickname: String,
                       customerId: Option[Int] = None,
                       discountApplied: Boolean = false)


class CoffeeOrdersTable(tag: Tag) extends Table[CoffeeOrder](tag, "COFFEE_ORDERS") {

  def orderId: Rep[Int] = column("ORDER_ID", O.PrimaryKey)
  def name: Rep[String] = column("NAME")
  def price: Rep[BigDecimal] = column("PRICE")
  def nickname: Rep[String] = column("NICKNAME")
  def discountApplied: Rep[Boolean] = column("DISCOUNT_APPLIED")
  def customerId: Rep[Option[Int]] = column("CUSTOMER_ID")

  // Это объявление нужно, т.к. для простоты схему данных мы генерируем с помощью slick. В продакшене для манипуляций со
  // схемой данных чаще всего используют специальный инструменты, например, flyway или liquibase.
  def customer: ForeignKeyQuery[CustomersTable, Customer] =
    foreignKey("CUSTOMER_FK", customerId, CustomersQueryRepository.AllCustomers)(_.id.?)

  override def * : ProvenShape[CoffeeOrder] = (orderId, name, price, nickname, customerId, discountApplied).mapTo[CoffeeOrder]
}

object CoffeeOrderQueryRepository {
  val AllOrders = TableQuery[CoffeeOrdersTable]

  def listCustomerOrders(customerId: Int): DIO[Seq[CoffeeOrder], Effect.Read] =
    AllOrders
      .filter(_.customerId === customerId)
      .result

  def findOrder(orderId: Int): DIO[Option[CoffeeOrder], Effect.Read] =
    AllOrders
      .filter(_.orderId === orderId)
      .result
      .headOption

  def addOrder(order: CoffeeOrder): DIO[Int, Effect.Write] =
    AllOrders += order

  def updateNickname(orderId: Int, nickname: String): DIO[Int, Effect.Write] =
    AllOrders
      .filter(_.orderId === orderId)
      .map(_.nickname)
      .update(nickname)

  def listTopNCoffees(n: Int): DIO[Seq[(String, Int)], Effect.Read] =
    AllOrders
      .groupBy(_.name)
      .map { case (name, group) => (name, group.length) }
      .sortBy { case (_, count) => count.desc }
      .take(n)
      .result

  def listTopNCustomers(n: Int): DIO[Seq[(Int, BigDecimal)], Effect.Read] =
    AllOrders
      .groupBy(_.customerId)
      .map { case (customerId, group) => (customerId, group.map(_.price).sum) }
      .filter { case (customerId, sum) => customerId.isDefined && sum.isDefined }
      .sortBy { case (_, sum) => sum.desc }
      .take(n)
      .map { case (customerId, sum) => (customerId.get, sum.get) }
      .result

  def listTopNNicknames(n: Int): DIO[Seq[(String, Int)], Effect.Read] =
    AllOrders
      .groupBy(_.nickname)
      .map { case (nickname, group) => (nickname, group.length) }
      .sortBy { case (_, count) => count.desc }
      .take(n)
      .result

  def countOrdersWithRealCustomerNames: DIO[Int, Effect.Read] =
    AllOrders
      .join(AllCustomers)
      .on(_.customerId === _.id)
      .filter { case (order, customer) => order.nickname === customer.name }
      .length
      .result

  def applyDiscount(id: Int,
                    discounts: Map[String, BigDecimal])
                   (implicit ec: ExecutionContext): DIO[Boolean, Effect.Read with Effect.Write] = {
    val searchQuery: Query[CoffeeOrdersTable, CoffeeOrder, Seq] =
      AllOrders.filter(o => o.orderId === id && !o.discountApplied)

    searchQuery.result.headOption.flatMap({
      case None => DIO.successful(false)
      case Some(order) =>
        val newPrice = discounts.get(order.name).map(disc => (1 - disc) * order.price)

        newPrice match {
          case None => DIO.successful(false)
          case Some(value) => searchQuery
            .map(o => (o.price, o.discountApplied))
            .update((value, true))
            .map(_ == 1)
        }
    })
  }

}