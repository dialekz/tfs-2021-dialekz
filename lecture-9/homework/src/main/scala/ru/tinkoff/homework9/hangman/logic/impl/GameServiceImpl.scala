package ru.tinkoff.homework9.hangman.logic.impl

import ru.tinkoff.homework9.hangman.exception.{GameAlreadyFinishedException, GameNotFoundException}
import ru.tinkoff.homework9.hangman.logic.{DictionaryService, GameService}
import ru.tinkoff.homework9.hangman.model.{Game, GameStatus, State}
import ru.tinkoff.homework9.hangman.storage.GameStorage

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class GameServiceImpl(dictionaryService: DictionaryService,
                      gameStorage: GameStorage)(implicit ec: ExecutionContext) extends GameService {

  override def find(gameId: Long): Future[Option[Game]] = gameStorage.find(gameId)

  override def startNewGame(userName: String): Future[Game] =
    gameStorage.insert(Instant.now(), State(userName, Set.empty, dictionaryService.chooseWord()), GameStatus.InProgress)

  override def makeGuess(id: Long, guess: Char): Future[Game] =
    gameStorage
      .find(id)
      .flatMap {
        case None => Future.failed[Game](GameNotFoundException(id))
        case Some(game) => makeGuess(game, guess)
      }

  private def makeGuess(game: Game, guess: Char): Future[Game] =
    game.status match {
      case GameStatus.Lost | GameStatus.Won => Future.failed[Game](GameAlreadyFinishedException(game.id, game.status))
      case GameStatus.InProgress =>
        val newState = game.state.addChar(guess)
        val newStatus = newState match {
          case s if s.playerWon => GameStatus.Won
          case s if s.playerLost => GameStatus.Lost
          case _ => GameStatus.InProgress
        }
        gameStorage.update(game.copy(state = newState, status = newStatus))
    }

}
