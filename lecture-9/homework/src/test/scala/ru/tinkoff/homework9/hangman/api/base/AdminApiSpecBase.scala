package ru.tinkoff.homework9.hangman.api.base

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec
import ru.tinkoff.homework9.hangman.logic.GameService
import ru.tinkoff.homework9.hangman.model.{Game, GameStatus, State}

import java.time.Instant
import scala.concurrent.Future

trait AdminApiSpecBase extends AnyFunSpec with ScalatestRouteTest with MockFactory {
  def route: Route

  import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

  describe("GET /admin/game/1") {
    it("возвращает текущее состояние игры с немаскированным словом") {
      (mockGameService.find _)
        .expects(1)
        .returns(Future.successful(Some(sampleGame)))

      Get("/admin/game/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Game]].contains(sampleGame))
      }
    }

    it("возвращает пустое тело, если игра не найдена") {
      (mockGameService.find _)
        .expects(1)
        .returns(Future.successful(None))

      Get("/admin/game/1") ~> route ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[Option[Game]].isEmpty)
      }
    }
  }


  private val sampleGame = Game(
    id = 1,
    startedAt = Instant.now(),
    state = State(
      name = "player",
      guesses = Set(),
      word = "word"
    ),
    status = GameStatus.InProgress
  )

  val mockGameService: GameService = mock[GameService]

}
