package ru.tinkoff.lecture6.future.assignment.bcrypt

import com.github.t3hnar.bcrypt.BCryptStrOps
import com.typesafe.scalalogging.StrictLogging
import ru.tinkoff.lecture6.future.assignment.util.ExecutionLogging

import scala.concurrent.{ExecutionContext, Future, blocking}

trait AsyncBcrypt {

  def hash(password: String, rounds: Int = 12)(implicit executionContext: ExecutionContext): Future[String]

  def verify(password: String, hash: String)(implicit executionContext: ExecutionContext): Future[Boolean]

}

class AsyncBcryptImpl extends AsyncBcrypt with StrictLogging with ExecutionLogging {

  override def hash(password: String, rounds: Int)
                   (implicit executionContext: ExecutionContext): Future[String] =
    Future {
      withExecutionLogging(s"hashing $password")(blocking(password.bcryptBounded(rounds)))
    }

  override def verify(password: String, hash: String)
                     (implicit executionContext: ExecutionContext): Future[Boolean] =
    Future {
      withExecutionLogging(s"verifying $password")(blocking(password.isBcryptedBounded(hash)))
    }

}