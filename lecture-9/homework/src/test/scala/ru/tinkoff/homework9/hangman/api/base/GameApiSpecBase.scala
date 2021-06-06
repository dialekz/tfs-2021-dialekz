package ru.tinkoff.homework9.hangman.api.base

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec
import ru.tinkoff.homework9.hangman.exception.{ExceptionResponse, GameAlreadyFinishedException, GameNotFoundException}
import ru.tinkoff.homework9.hangman.logic.PlayerGameService
import ru.tinkoff.homework9.hangman.model.{Game, GameStatus, State}

import java.time.Instant
import scala.concurrent.Future

trait GameApiSpecBase extends AnyFunSpec with MockFactory with ScalatestRouteTest {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  def route: Route

  describe("GET http://localhost:8080/game/{gameId}") {
    it("возвращает текущее состояние игры с маскированным словом") {
      (mockGameService.find _)
        .expects(1)
        .returns(Future.successful(Some(sampleGame)))

      Get("/game/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Game]].contains(sampleGame))
      }
    }

    it("возвращает пустое тело, если игра не найдена") {
      (mockGameService.find _)
        .expects(1)
        .returns(Future.successful(None))

      Get("/game/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Game]].isEmpty)
      }
    }
  }

  describe("POST http://localhost:8080/game/{gameId}/guess?letter={guess}") {
    it("возвращает измененную игру") {
      (mockGameService.makeGuess _)
        .expects(1, 'a')
        .returns(Future.successful(sampleGame))

      Post("/game/1/guess?letter=a") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Game] == sampleGame)
      }
    }

    it("возвращает код 400 и текст ошибки в виде json если игра не найдена") {
      (mockGameService.makeGuess _)
        .expects(1, 'A')
        .returns(Future.failed(GameNotFoundException(1)))

      Post("/game/1/guess?letter=A") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse("Game with id=1 not found"))
      }
    }

    it("возвращает код 400 и текст ошибки в виде json если игра уже окончена") {
      (mockGameService.makeGuess _)
        .expects(1, 'A')
        .returns(Future.failed(GameAlreadyFinishedException(1, GameStatus.Won)))

      Post("/game/1/guess?letter=A") ~> route ~> check {
        assert(status == StatusCodes.BadRequest)
        assert(responseAs[ExceptionResponse] == ExceptionResponse("Game with id=1 already finished. You Won!"))
      }
    }
  }

  describe("POST game?userName={playerName}") {
    it("возвращает новую игру") {
      (mockGameService.startNewGame _)
        .expects("player")
        .returns(Future.successful(sampleGame))

      Post("/game?userName=player") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Game] == sampleGame)
      }
    }
  }

  protected val mockGameService: PlayerGameService = mock[PlayerGameService]

  private val sampleGame = Game(
    id = 1,
    startedAt = Instant.now(),
    state = State(
      name = "player",
      guesses = Set(),
      word = "a***"
    ),
    status = GameStatus.InProgress
  )
}
