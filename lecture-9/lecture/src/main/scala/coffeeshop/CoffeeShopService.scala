package coffeeshop

import java.util.UUID
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

class CoffeeShopService {
  def makeCoffee(coffeeOrder: CoffeeOrder): Future[UUID] = {
    val orderId = UUID.randomUUID()
    orderStore.put(orderId, coffeeOrder)
    Future.successful(orderId)
  }

  def getCoffeeOrder(id: UUID): Future[CoffeeOrder] =
    Future.successful(orderStore(id))

  private val orderStore = TrieMap[UUID, CoffeeOrder]()
}
