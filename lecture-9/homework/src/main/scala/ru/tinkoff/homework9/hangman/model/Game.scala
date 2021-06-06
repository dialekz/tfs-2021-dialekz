package ru.tinkoff.homework9.hangman.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.Instant

case class Game(id: Long,
                startedAt: Instant,
                state: State,
                status: GameStatus)

object Game {
  implicit val jsonDecoder: Decoder[Game] = deriveDecoder
  implicit val jsonEncoder: Encoder[Game] = deriveEncoder
}