package ru.tinkoff.lecture6.future.assignment.store

import scala.concurrent.Future

trait AsyncCredentialStore {
  /**
    * возвращает хеш пользовательского пароля
    */
  def find(user: String): Future[Option[String]]
}
