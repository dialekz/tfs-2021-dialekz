package ru.tinkoff.lecture6.future.assignment.assignment

import com.typesafe.scalalogging.StrictLogging
import ru.tinkoff.lecture6.future.assignment.bcrypt.AsyncBcrypt
import ru.tinkoff.lecture6.future.assignment.store.AsyncCredentialStore
import ru.tinkoff.lecture6.future.assignment.util.Scheduler

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future, Promise, TimeoutException}
import scala.util.{Failure, Success}

class Assignment(bcrypt: AsyncBcrypt, credentialStore: AsyncCredentialStore)
                (implicit executionContext: ExecutionContext) extends StrictLogging {

  /**
   * проверяет пароль для пользователя
   * возвращает Future со значением false:
   *   - если пользователь не найден
   *   - если пароль не подходит к хешу
   */
  def verifyCredentials(user: String, password: String): Future[Boolean] =
    for {
      hash <- credentialStore.find(user)
      result <- hash match {
        case Some(value) => bcrypt.verify(password, value)
        case None => Future.successful(false)
      }
    } yield result

  /**
   * выполняет блок кода, только если переданы верные учетные данные
   * возвращает Future c ошибкой InvalidCredentialsException, если проверка не пройдена
   */
  def withCredentials[A](user: String, password: String)(block: => A): Future[A] =
    for {
      verified <- verifyCredentials(user, password)
      result <- if (verified) Future(block) else Future.failed(new InvalidCredentialsException)
    } yield result

  /**
   * хеширует каждый пароль из списка и возвращает пары пароль-хеш
   */
  def hashPasswordList(passwords: Seq[String]): Future[Seq[(String, String)]] =
    Future.traverse(passwords)(p => bcrypt.hash(p).map((p, _)))

  /**
   * проверяет все пароли из списка против хеша, и если есть подходящий - возвращает его
   * если подходит несколько - возвращается любой
   */
  def findMatchingPassword(passwords: Seq[String], hash: String): Future[Option[String]] =
    for {
      seq <- Future.traverse(passwords)(p => bcrypt.verify(p, hash).map((p, _)))
      result <- Future.successful(seq.collectFirst({ case (pass, true) => pass }))
    } yield result

  /**
   * логирует начало и окончание выполнения Future, и продолжительность выполнения
   */
  def withLogging[A](f: => Future[A]): Future[A] = {
    println("Start feature with logging")
    val st = System.currentTimeMillis()
    f.andThen(_ => println(s"Finish feature with logging\nCompletion time: ${System.currentTimeMillis() - st} mls"))
  }

  /**
   * пытается повторно выполнить f retries раз, до первого успеха
   * если все попытки провалены, возвращает первую ошибку
   *
   * Важно: f не должна выполняться большее число раз, чем необходимо
   */
  def withRetry[A](f: => Future[A], retries: Int): Future[A] = f.transformWith {
    case Success(value) => Future.successful(value)
    case Failure(ex) =>
      if (retries > 1) {
        withRetry(f, retries - 1).recover({ _ => throw new Exception(s"First: ${ex.getMessage}") })
      } else {
        Future.failed(new Exception(s"First: ${ex.getMessage}"))
      }
  }

  /**
   * по истечению таймаута возвращает Future.failed с java.util.concurrent.TimeoutException
   */
  def withTimeout[A](f: Future[A], timeout: FiniteDuration): Future[A] = {
    val p = Promise[A]()
    val cancelable = Scheduler.executeAfter(timeout) {
      p.tryFailure(new TimeoutException)
    }

    f.map({ a => if (p.trySuccess(a)) cancelable.cancel() })
      .recover({ case e: Exception => if (p.tryFailure(e)) cancelable.cancel() })

    p.future
  }

  /**
   * делает то же, что и hashPasswordList, но дополнительно:
   *   - каждая попытка хеширования отдельного пароля выполняется с таймаутом
   *   - при ошибке хеширования отдельного пароля, попытка повторяется в пределах retries (свой на каждый пароль)
   *   - возвращаются все успешные результаты
   */
  def hashPasswordListReliably(passwords: Seq[String],
                               retries: Int,
                               timeout: FiniteDuration
                              ): Future[Seq[(String, String)]] = {
    def hashWithTimeout(pass: String): Future[String] = withTimeout(bcrypt.hash(pass), timeout)
    def hashWithTimeoutAndRetry(pass: String): Future[String] = withRetry(hashWithTimeout(pass), retries)

    def fMap(pass: String): Future[Option[(String, String)]] = hashWithTimeoutAndRetry(pass).transform {
      case Success(value) => Success(Option((pass, value)))
      case Failure(_) => Success(None)
    }

    Future.traverse(passwords)(fMap)
      .map(seq => seq.collect({ case Some(value) => value }))
  }
}
