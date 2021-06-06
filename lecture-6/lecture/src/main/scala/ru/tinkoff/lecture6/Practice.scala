package ru.tinkoff.lecture6

import java.io.Closeable
import java.util.concurrent.Executors
import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.io.{BufferedSource, Source}

case class Bucket(min: Int, max: Int, count: Int)


case class Stats(lineCount: Int,
                 minLineLength: Int,
                 averageLineLength: Int,
                 maxLineLength: Int,
                 buckets: Seq[Bucket])


@nowarn
object Practice extends App {

  private val bucketSize = 10
  private val defaultFileName = "in.txt"

  private def getSource(fileName: String): BufferedSource =
    Source.fromResource(fileName)

  private def read(in: BufferedSource): Future[Iterator[String]] =
    Future(blocking(in.getLines()))

  private def printStats(stats: Stats): Unit = {
    import stats._
    println(
      s"""
         | Total line count: $lineCount
         | min: $minLineLength
         | avg: $averageLineLength
         | max: $maxLineLength
         |
         | buckets:
         |${
        buckets.map { b =>
          import b._
          s"   - $min-$max: $count"
        }.mkString("\n")
      }""".stripMargin)
  }

  /*
    Методы для разминки
   */

  def asyncWithResource[R <: Closeable, T](resource: R)(code: R => Future[T]): Future[T] = {
    code(resource)
      .andThen { case _ => resource.close() }
  }

  def asyncCountLines: Future[Int] = asyncWithResource(getSource(defaultFileName)) { source =>
    for {
      iterator <- read(source)
    } yield iterator.size
  }

  def asyncLineLengths: Future[Seq[(String, Int)]] = asyncWithResource(getSource(defaultFileName)) { source =>
    for {
      iterator <- read(source)
    } yield iterator.map(line => line -> line.length).toSeq
  }

  def asyncTotalLength: Future[Int] = asyncWithResource(getSource(defaultFileName)) { source =>
    for {
      iterator <- read(source)
    } yield iterator.map(_.length).sum
  }

  def countShorterThan(maxLength: Int): Future[Int] =
    asyncLineLengths
      .map(linesWithLength => linesWithLength.count(_._2 < maxLength))


  /*
    Sleep sort
    https://www.quora.com/What-is-sleep-sort
   */

  val scheduller = Executors.newSingleThreadScheduledExecutor()

  def printWithDelay(delay: FiniteDuration, s: String): Future[Unit] = {
    val promise = Promise[Unit]()
    scheduller
      .schedule(() => promise.success(println(s)), delay.length, delay.unit)
    promise.future
  }

  def sleepSort: Future[Unit] =
    for {
      lines <- asyncLineLengths
      _ <- Future
        .traverse(lines) { e => printWithDelay(e._2.millis * 10, e._1)
        }
    } yield ()


  /*
    Calculate file statistics
   */

  def splitToBuckets(linesWithLengths: Seq[(String, Int)]): Future[Seq[Bucket]] = {
    Future.successful(Seq(Bucket(0, 100, 10)))
  }

  def calculateStats: Future[Stats] = {
    for {
      lengths <- asyncLineLengths
      buckets <- splitToBuckets(lengths)
    } yield Stats(
      lengths.size,
      lengths.map(_._2).min,
      lengths.map(_._2).sum / lengths.size,
      lengths.map(_._2).max,
      buckets
    )
  }


  val result = Await.result(sleepSort, 100.seconds)
  scheduller.shutdown()
  println(result)

}
