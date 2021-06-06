package ru.tinkoff.lecture7

import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@nowarn
object Composability_06 extends App {

  case class User(id: String)

  def getFacebookProfile(name: String): Future[User] =
    Future.successful(User("1"))
      .andThen { case _ => println("call facebook")}

  def getVkProfile(name: String): Future[User] =
    Future.successful(User("1"))
      .andThen { case _ => println("call vk")}

  // Всегда вызывает оба api :(
  def getProfile(userName: String): Future[User] =
    getFacebookProfile(userName)
      .fallbackTo(getVkProfile(userName))

  val user = Await.result(getProfile("foo"), Duration.Inf)

  println(user)
}
