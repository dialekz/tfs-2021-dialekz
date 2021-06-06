package ru.tinkoff.homework9.hangman.api

import akka.http.scaladsl.server.Route
import ru.tinkoff.homework9.hangman.api.base.GameApiSpecBase
import ru.tinkoff.homework9.hangman.exception.HangmanExceptionHandler

class AkkaHttpGameApiSpec extends GameApiSpecBase {
  override val route = Route.seal(
    new GameApi(mockGameService).route
  )(exceptionHandler = HangmanExceptionHandler.exceptionHandler) // ExceptionHandler - обрабатывает ошибки, которые произошли при обработке запроса


}
