package rdbms

import org.scalatest.matchers.should.Matchers
import rdbms.CustomersQueryRepository._
import slick.jdbc.H2Profile.api._

class CustomersQueryRepositoryTest extends CoffeeShopDatabaseSuite with Matchers {

  test("getCustomersCount should return customers count") {
    for {
      count <- getCustomersCount
    } yield assert(count == 2)
  }

  test("findCustomer") {
    for {
      customer <- findCustomer(1)
    } yield assert(customer.contains(SampleCustomers.head))
  }

  test("markEmailConfirmed") {
    val newCustomer = Customer(3, "Harry", "potter@harry.com", emailConfirmed = false)
    for {
      _ <- AllCustomers += newCustomer
      _ <- markEmailConfirmed(newCustomer.id)
      customer <- findCustomer(newCustomer.id)
    } yield assert(customer.contains(newCustomer.copy(emailConfirmed = true)))
  }

}
