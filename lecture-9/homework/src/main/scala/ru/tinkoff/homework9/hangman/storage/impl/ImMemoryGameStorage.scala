package ru.tinkoff.homework9.hangman.storage.impl

import ru.tinkoff.homework9.hangman.exception.GameNotFoundException
import ru.tinkoff.homework9.hangman.model.{Game, GameStatus, State}
import ru.tinkoff.homework9.hangman.storage.GameStorage

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future


/**
 * In-memory хранилище для состояния игры на основе TrieMap
 */
class ImMemoryGameStorage extends GameStorage {

  private val index = new AtomicLong(1);
  private val storage = new TrieMap[Long, Game]

  override def find(id: Long): Future[Option[Game]] = Future.successful(storage.get(id))

  override def insert(startedAt: Instant,
                      state: State,
                      status: GameStatus): Future[Game] = {
    val game = Game(index.getAndIncrement(), startedAt, state, status)
    Future.successful(
      storage
        .addOne(game.id -> game)
        .apply(game.id)
    )
  }

  override def update(game: Game): Future[Game] =
    storage.replace(game.id, game) match {
      case None => Future.failed(GameNotFoundException(game.id))
      case Some(_) => Future.successful(game)
    }
}
