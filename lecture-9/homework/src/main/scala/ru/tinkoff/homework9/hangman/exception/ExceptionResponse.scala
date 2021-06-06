package ru.tinkoff.homework9.hangman.exception

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ExceptionResponse(errorMessage: String)

object ExceptionResponse {
  implicit val jsonEncoder: Encoder[ExceptionResponse] = deriveEncoder
  implicit val jsonDecoder: Decoder[ExceptionResponse] = deriveDecoder
}
