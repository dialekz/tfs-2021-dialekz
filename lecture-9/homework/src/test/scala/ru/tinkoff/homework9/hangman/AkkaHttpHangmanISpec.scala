package ru.tinkoff.homework9.hangman

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import ru.tinkoff.homework9.hangman.base.HangmanISpecBase

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Future}


class AkkaHttpHangmanISpec extends HangmanISpecBase {
  implicit lazy val ac: ActorSystem = ActorSystem()
  implicit lazy val ec: ExecutionContext = ac.dispatcher

  override protected def beforeAll(): Unit = {
    Await.result(hangmanGame, Duration.Inf)
    ()
  }

  override protected def afterAll(): Unit = {
    Await.result(
      hangmanGame.flatMap(_.terminate(10.seconds))
        .flatMap(_ => ac.terminate()),
      Duration.Inf
    )
    ()
  }

  private lazy val hangmanGame: Future[Http.ServerBinding] = HangmanGame().start()
}
