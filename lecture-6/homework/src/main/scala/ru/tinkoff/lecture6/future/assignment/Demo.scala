package ru.tinkoff.lecture6.future.assignment

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus.{mapValueReader, stringValueReader, toFicusConfig}
import ru.tinkoff.lecture6.future.assignment.assignment.Assignment
import ru.tinkoff.lecture6.future.assignment.bcrypt.AsyncBcryptImpl
import ru.tinkoff.lecture6.future.assignment.store.AsyncCredentialStore

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn.readLine

class ConfigCredentialStore(config: Config) extends AsyncCredentialStore {
  private val credentialMap = config.as[Map[String, String]]("credentials")
  override def find(user: String): Future[Option[String]] = Future.successful(credentialMap.get(user))
}

object Demo extends App {
  val config: Config = ConfigFactory.load()
  val credentialStore = new ConfigCredentialStore(config)
  val reliableBcrypt = new AsyncBcryptImpl
  val assignment = new Assignment(reliableBcrypt, credentialStore)

  assignment.withLogging(Future.successful(Thread.sleep(500)))

  readLine()

}
