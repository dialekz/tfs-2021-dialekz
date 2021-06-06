package concurrency

import java.util.concurrent.atomic.AtomicInteger

class CoronaVisitorGuard(maxVisitors: Int) {
  var current = new AtomicInteger(0)

  def tryEnter(): Boolean = {
    val oldCurrent = current
      .getAndUpdate { currentValue: Int => if (currentValue < maxVisitors) currentValue + 1 else currentValue }
    oldCurrent < maxVisitors
  }

  def getCurrentVisitorsCount(): Int = current.get()

  def leave(): Int = current.decrementAndGet()
}
