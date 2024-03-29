package ru.tinkoff.homework9.hangman.exception

import ru.tinkoff.homework9.hangman.model.GameStatus

sealed abstract class HangmanException(message: String) extends Exception(message)

final case class GameNotFoundException(gameId: Long)
  extends HangmanException(s"Game with id=$gameId not found")

final case class GameAlreadyFinishedException(gameId: Long, status: GameStatus)
  extends HangmanException(s"Game with id=$gameId already finished. You $status!")
