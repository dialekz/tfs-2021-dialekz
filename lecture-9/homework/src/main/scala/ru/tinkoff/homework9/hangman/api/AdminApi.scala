package ru.tinkoff.homework9.hangman.api

import akka.http.scaladsl.server.Route
import ru.tinkoff.homework9.hangman.logic.GameService

class AdminApi(gameService: GameService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport.marshaller

  val route: Route =
    (get & path("admin" / "game" / LongNumber)) {
      gameId => complete(gameService.find(gameId))
    }
}
