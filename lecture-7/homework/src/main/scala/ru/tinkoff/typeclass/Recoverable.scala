package ru.tinkoff.typeclass

import java.util.concurrent.CompletableFuture
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

trait Recoverable[F[_]] {
  def `new`[T](value: T): F[T]

  def map[A, B](fa: F[A])(f: A => B): F[B]

  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  def recover[A](fa: F[A])(pf: PartialFunction[Throwable, A]): F[A]

  def recoverWith[A](fa: F[A])(pf: PartialFunction[Throwable, F[A]]): F[A]

  def raiseError[A](e: Throwable): F[A]
}

object Recoverable {

  implicit class RecoverableOps[F[_], A](fa: F[A])(implicit F: Recoverable[F]) {
    def map[B](f: A => B): F[B] = F.map(fa)(f)

    def flatMap[B](f: A => F[B]): F[B] = F.flatMap(fa)(f)

    def recover(pf: PartialFunction[Throwable, A]): F[A] = F.recover(fa)(pf)

    def recoverWith(pf: PartialFunction[Throwable, F[A]]): F[A] = F.recoverWith(fa)(pf)
  }

  implicit def recoverableForTry: Recoverable[Try] = new Recoverable[Try] {
    override def `new`[T](value: T): Try[T] = Try(value)

    override def map[A, B](fa: Try[A])(f: A => B): Try[B] = fa.map(f)

    override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)

    override def recover[A](fa: Try[A])(pf: PartialFunction[Throwable, A]): Try[A] = fa.recover(pf)

    override def recoverWith[A](fa: Try[A])(pf: PartialFunction[Throwable, Try[A]]): Try[A] = fa.recoverWith(pf)

    override def raiseError[A](e: Throwable): Try[A] = Failure(e)
  }

  implicit def recoverableForFuture(implicit ec: ExecutionContext): Recoverable[Future] = new Recoverable[Future] {
    override def `new`[T](value: T): Future[T] = Future.successful(value)

    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)

    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)

    override def recover[A](fa: Future[A])(pf: PartialFunction[Throwable, A]): Future[A] = fa.recover(pf)

    override def recoverWith[A](fa: Future[A])(pf: PartialFunction[Throwable, Future[A]]): Future[A] = fa.recoverWith(pf)

    override def raiseError[A](e: Throwable): Future[A] = Future.failed(e)
  }

  implicit def recoverableForCompletableFuture: Recoverable[CompletableFuture] = new Recoverable[CompletableFuture] {
    override def `new`[T](value: T): CompletableFuture[T] = CompletableFuture.completedFuture(value)

    override def map[A, B](fa: CompletableFuture[A])(f: A => B): CompletableFuture[B] = fa.thenApply(f(_))

    override def flatMap[A, B](fa: CompletableFuture[A])(f: A => CompletableFuture[B]): CompletableFuture[B] = fa.thenCompose(f(_))

    override def recover[A](fa: CompletableFuture[A])(pf: PartialFunction[Throwable, A]): CompletableFuture[A] = fa.exceptionally(pf(_))

    override def recoverWith[A](fa: CompletableFuture[A])(pf: PartialFunction[Throwable, CompletableFuture[A]]): CompletableFuture[A] = {
      fa.handle(
        (value, ex) => Option(ex) match {
          case None => CompletableFuture.completedFuture(value)
          case Some(e) => pf(e)
        })
        .thenCompose(identity(_))
    }

    override def raiseError[A](e: Throwable): CompletableFuture[A] = CompletableFuture.failedFuture(e)
  }
}