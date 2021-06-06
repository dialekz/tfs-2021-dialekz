package concurrency

import scala.collection.concurrent.TrieMap


class Bag {
  private val things = TrieMap[String, Double]()

  def putThing(what: String, price: Double): Unit = {
    things.put(what, price)
    ()
  }

  def size: Int = things.size
}
