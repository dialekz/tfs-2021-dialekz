package ru.tinkoff.lecture7

import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future, blocking}

object BatchTraverseTask extends App {

  def slowCalculation(i: Int)(implicit ec: ExecutionContext): Future[Int] = Future {
    blocking {
      println(i)
      Thread.sleep(1000)
      i
    }
  }

  def batchTraverse[A, B](input: Seq[A], batchSize: Int) // Какая тут проблема?
                         (f: A => Task[B]): Task[Seq[B]] = {

    if (input.isEmpty) Task.now(Seq())
    else {
      val batches = input.grouped(batchSize)

      batches
        .map(batch => Task.parTraverse(batch)(f))
        .foldLeft(Task.now(Seq[B]())) { (accF, batchF) =>
          for {
            acc <- accF
            batch <- batchF
          } yield acc ++ batch
        }
    }
  }

  import monix.execution.Scheduler.Implicits.global

  def calculation(i: Int): Task[Int] = Task.fromFuture(slowCalculation(i))

  val value: Task[Seq[Int]] = batchTraverse(1 to 12, 4)(calculation)
  value
    .onErrorFallbackTo(value)
//  Await.result(value.runToFuture, Duration.Inf)
}
