package rdbms

import slick.dbio.Effect
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape


case class Customer(id: Int,
                    name: String,
                    email: String,
                    emailConfirmed: Boolean)

class CustomersTable(tag: Tag) extends Table[Customer](tag, "CUSTOMERS") {
  def id: Rep[Int] = column("ID", O.PrimaryKey)
  def name: Rep[String] = column("NAME")
  def email: Rep[String] = column("EMAIL", O.Unique)
  def emailConfirmed: Rep[Boolean] = column("EMAIL_CONFIRMED")

  override def * : ProvenShape[Customer] = (id, name, email, emailConfirmed).mapTo[Customer]
}

object CustomersQueryRepository {
  val AllCustomers = TableQuery[CustomersTable]

  def findCustomer(id: Int): DIO[Option[Customer], Effect.Read] =
    AllCustomers
      .filter(_.id === id)
      .result
      .headOption

  def markEmailConfirmed(id: Int): DIO[Int, Effect.Write] =
    AllCustomers
      .filter(_.id === id)
      .map(_.emailConfirmed)
      .update(true)


  val getCustomersCount: DIO[Int, Effect.Read] = AllCustomers.length.result
}