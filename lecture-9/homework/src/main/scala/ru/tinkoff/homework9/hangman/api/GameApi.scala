package ru.tinkoff.homework9.hangman.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import ru.tinkoff.homework9.hangman.logic.PlayerGameService

class GameApi(gameService: PlayerGameService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  implicit val charUnmarshaller: Unmarshaller[String, Char] =
    Unmarshaller.strict[String, Char] { string =>
      if (string.length == 1) string.head
      else throw new IllegalArgumentException("Got string or nothing but char expected")
    }

  val getGameRoute: Route =
    (get & path("game" / LongNumber)) {
      gameId => complete(gameService.find(gameId))
    }

  val makeGuessRoute: Route =
    (post & path("game" / LongNumber / "guess") & parameters("letter".as[Char])) {
      (gameId, letter) => complete(gameService.makeGuess(gameId, letter))
    }

  val createNewGameRoute: Route =
    (post & path("game") & parameters("userName".as[String])) {
      userName => complete(gameService.startNewGame(userName))
    }

  val route: Route = {
    getGameRoute ~ makeGuessRoute ~ createNewGameRoute
  }

}
