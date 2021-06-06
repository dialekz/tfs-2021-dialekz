package ru.tinkoff.homework9.hangman.api

import akka.http.scaladsl.server.Route
import ru.tinkoff.homework9.hangman.api.base.AdminApiSpecBase
import ru.tinkoff.homework9.hangman.exception.HangmanExceptionHandler

class AkkaHttpAdminApiSpec extends AdminApiSpecBase {

  override val route: Route = Route.seal(
    new AdminApi(mockGameService).route
  )(exceptionHandler = HangmanExceptionHandler.exceptionHandler)  // ExceptionHandler - обрабатывает ошибки, которые произошли при обработке запрос)

}
