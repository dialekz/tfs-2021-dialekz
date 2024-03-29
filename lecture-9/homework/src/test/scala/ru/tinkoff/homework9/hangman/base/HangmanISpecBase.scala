package ru.tinkoff.homework9.hangman.base

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import akka.http.scaladsl.model.{HttpMethod, HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AsyncFunSuite
import ru.tinkoff.homework9.hangman.model.{Game, GameStatus, State}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

trait HangmanISpecBase extends AsyncFunSuite with BeforeAndAfterAll {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  implicit def ac: ActorSystem
  implicit def ec: ExecutionContext

  test("Player can win hangman game") {
    for {
      initialGame <- startGame()
      _ = println(initialGame.state)
      _ = assert(initialGame.state.word.forall(_ == '*'))
      _ = assert(initialGame.status == GameStatus.InProgress)
      realGame <- getGameFromAdmin(initialGame.id)
      realWord = realGame.state.word
      guesses = realWord.toList.distinct
      gameHistory <- makeGuesses(initialGame.id, guesses)
      finishedGame <- getGame(initialGame.id)
    } yield {
      val stateHistory = gameHistory.map(_.state)
      val expectedStates = recreateStates(guesses, realGame.state)
      assert(expectedStates == stateHistory)
      val expectedGameState = initialGame.copy(state = State(playerName, guesses.toSet, realWord), status = GameStatus.Won)
      assert(expectedGameState == finishedGame)
    }
  }

  test("Player can lost hangman game") {
    for {
      initialGame <- startGame()
      _ = assert(initialGame.state.word.forall(_ == '*'))
      _ = assert(initialGame.status == GameStatus.InProgress)
      realGame <- getGameFromAdmin(initialGame.id)
      realWord = realGame.state.word
      guesses = ('a' to 'z').filterNot(c => realWord.contains(c)).take(10).toList
      gameHistory <- makeGuesses(initialGame.id, guesses)
      finishedGame <- getGame(initialGame.id)
    } yield {
      val stateHistory = gameHistory.map(_.state)
      val expectedStates = recreateStates(guesses, realGame.state)
      assert(expectedStates == stateHistory)
      val expectedGameState = initialGame.copy(state = initialGame.state.copy(guesses = guesses.toSet), status = GameStatus.Lost)
      assert(expectedGameState == finishedGame)

    }
  }

  private def recreateStates(guesses: Seq[Char], state: State) =
    guesses
      .scanLeft(List.empty[Char])(_.prepended(_))
      .map(guesses => state.word.map { c => if (guesses.contains(c)) c else '*' } -> guesses)
      .map { case (word, guesses) => state.copy(word = word, guesses = guesses.toSet) }
      .tail

  private def makeGuesses(gameId: Long, guesses: Seq[Char]): Future[Seq[Game]] =
    guesses
      .foldLeft(Future.successful(Seq[Game]()))((acc, guess) =>
        acc.flatMap(history => makeGuess(gameId, guess).map(history :+ _))
      )

  private def makeGuess(gameId: Long, guess: Char): Future[Game] =
    SimpleHttpClient.post[Game](Uri(s"http://localhost:8080/game/$gameId/guess?letter=$guess"))

  private def getGame(gameId: Long): Future[Game] =
    SimpleHttpClient.get[Game](Uri(s"http://localhost:8080/game/$gameId"))

  private def getGameFromAdmin(gameId: Long): Future[Game] =
    SimpleHttpClient.get[Game](Uri(s"http://localhost:8080/admin/game/$gameId"))

  private def startGame(): Future[Game] = {
    SimpleHttpClient.post[Game](Uri(s"http://localhost:8080/game?userName=$playerName"))
  }

  private val playerName = "player"
}

object SimpleHttpClient extends LazyLogging {
  def post[U: FromResponseUnmarshaller](uri: Uri)(implicit ac: ActorSystem, ec: ExecutionContext): Future[U] = {
    logRequest(uri, POST)
    Http().singleRequest(HttpRequest(POST, uri = uri))
      .flatMap(response => Unmarshal(response).to[U])
  }

  def get[U: FromResponseUnmarshaller](uri: Uri)(implicit ac: ActorSystem, ec: ExecutionContext): Future[U] = {
    logRequest(uri, GET)
    Http().singleRequest(HttpRequest(GET, uri = uri))
      .andThen { case Success(response) => logResponse(response) }
      .flatMap(response => Unmarshal(response).to[U])
  }

  private def logRequest(uri: Uri, method: HttpMethod): Unit =
    logger.debug(s"Http request sent: ${method.value} $uri")

  private def logResponse(response: HttpResponse): Unit =
    logger.debug(s"Received response: code=${response.status}")
}
