package ru.tinkoff.lecture7

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, blocking}

object BatchTraverseFuture extends App {

  def slowCalculation(i: Int)(implicit ec: ExecutionContext): Future[Int] = Future {
    blocking {
      println(i)
      Thread.sleep(1000)
      i
    }
  }

  def batchTraverse[A, B](input: Seq[A], batchSize: Int) // Какая тут проблема?
                         (f: A => Future[B])(implicit ec: ExecutionContext): Future[Seq[B]] = {

    if (input.isEmpty) Future.successful(Seq())
    else {
      val batches = input.grouped(batchSize)

      batches
        .map(batch => Future.traverse(batch)(f))
        .foldLeft(Future.successful(Seq[B]())) { (accF, batchF) =>
          for {
            acc <- accF
            batch <- batchF
          } yield acc ++ batch
        }
    }
  }

  import monix.execution.Scheduler.Implicits.global

  def calculation(i: Int) = slowCalculation(i)

  val value = batchTraverse(1 to 12, 4)(calculation)
  Await.result(value, Duration.Inf)
}
