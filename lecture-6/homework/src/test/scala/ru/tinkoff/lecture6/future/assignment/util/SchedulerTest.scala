package ru.tinkoff.lecture6.future.assignment.util

import java.util.concurrent.atomic.AtomicBoolean
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.lecture6.future.assignment.util.Scheduler

import scala.concurrent.duration._

class SchedulerTest extends AnyFlatSpec with Matchers {
  "scheduler" should "execute block after a delay" in {
    val complete = new AtomicBoolean()
    Scheduler.executeAfter(1.second)(complete.set(true))

    Thread.sleep(200) // Thread.sleep в тестах стоит избегать
    complete.get shouldBe false

    Thread.sleep(1500) // Thread.sleep в тестах стоит избегать
    complete.get shouldBe true
  }
}
