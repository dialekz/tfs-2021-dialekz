package ru.tinkoff.homework9.hangman.exception

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler

object HangmanExceptionHandler {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  val exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: HangmanException => complete(BadRequest, ExceptionResponse(e.getMessage))
    }
}