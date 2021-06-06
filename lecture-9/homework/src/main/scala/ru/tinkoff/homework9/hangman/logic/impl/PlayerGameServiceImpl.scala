package ru.tinkoff.homework9.hangman.logic.impl

import ru.tinkoff.homework9.hangman.logic.{GameService, PlayerGameService}
import ru.tinkoff.homework9.hangman.model.Game

import scala.concurrent.{ExecutionContext, Future}

/**
 * @inheritdoc ru.tinkoff.homework9.hangman.logic.impl.PlayerGameService
 */
class PlayerGameServiceImpl(delegate: GameService)(implicit ec: ExecutionContext) extends PlayerGameService {

  def find(gameId: Long): Future[Option[Game]] =
    delegate
      .find(gameId)
      .map {
        case Some(game) => Option(maskNonGuessedChars(game))
        case None => None
      }

  override def startNewGame(userName: String): Future[Game] =
    delegate
      .startNewGame(userName)
      .map(maskNonGuessedChars)

  override def makeGuess(id: Long, guess: Char): Future[Game] =
    delegate
      .makeGuess(id, guess)
      .map(maskNonGuessedChars)

  private def maskNonGuessedChars(game: Game): Game = game.copy(state = game.state.convertToMasked)
}
