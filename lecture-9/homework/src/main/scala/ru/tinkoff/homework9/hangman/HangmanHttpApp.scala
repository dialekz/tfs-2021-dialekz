package ru.tinkoff.homework9.hangman

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import com.typesafe.scalalogging.LazyLogging
import ru.tinkoff.homework9.hangman.api.{AdminApi, GameApi}
import ru.tinkoff.homework9.hangman.exception.HangmanExceptionHandler
import ru.tinkoff.homework9.hangman.logic.impl.{GameServiceImpl, PlayerGameServiceImpl}
import ru.tinkoff.homework9.hangman.logic.{DictionaryService, GameService, PlayerGameService}
import ru.tinkoff.homework9.hangman.storage.GameStorage
import ru.tinkoff.homework9.hangman.storage.impl.ImMemoryGameStorage

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object HangmanHttpApp {
  implicit val ac: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = ac.dispatcher

  def main(args: Array[String]): Unit = {
    Await.result(HangmanGame().start(), Duration.Inf)
    ()
  }
}

case class HangmanGame()(implicit ac: ActorSystem, ec: ExecutionContext) extends LazyLogging {

  private val dictionaryService = new DictionaryService
  private val storage: GameStorage = new ImMemoryGameStorage
  private val gameService: GameService = new GameServiceImpl(dictionaryService, storage)
  private val playerGameService: PlayerGameService = new PlayerGameServiceImpl(gameService)
  private val gameRoute: GameApi = new GameApi(playerGameService)
  private val adminRoute: AdminApi = new AdminApi(gameService)
  private val routes = Route.seal(
    RouteConcatenation.concat(
      gameRoute.route,
      adminRoute.route
    )
  )(exceptionHandler = HangmanExceptionHandler.exceptionHandler)

  def start(): Future[Http.ServerBinding] =
    Http()
      .newServerAt("localhost", 8080)
      .bind(routes)
      .andThen { case b => logger.info(s"server started at: $b") }
}