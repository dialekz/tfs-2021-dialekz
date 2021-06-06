package ru.tinkoff.homework9.hangman.model

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

final case class State(name: String, guesses: Set[Char], word: String) {
  def failures: Int = (guesses -- word.toSet).size

  def playerLost: Boolean = failures >= 10

  def playerWon: Boolean = (word.toSet -- guesses).isEmpty

  def addChar(char: Char): State = copy(guesses = guesses + char)

  def convertToMasked: State = copy(word = word.map(c => if (guesses.contains(c)) c else '*'))
}

object State {
  implicit val jsonDecoder: Decoder[State] = deriveDecoder
  implicit val jsonEncoder: Encoder[State] = deriveEncoder
}