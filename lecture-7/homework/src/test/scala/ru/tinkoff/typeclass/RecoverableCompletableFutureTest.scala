package ru.tinkoff.typeclass

import org.scalactic.Prettifier
import org.scalactic.source.Position
import org.scalatest.Assertion

import java.util.concurrent.{CompletableFuture, TimeUnit}
import scala.util.{Failure, Try}

class RecoverableCompletableFutureTest extends RecoverableTest[CompletableFuture] {
  override def assertEqualsF[L, R](left: CompletableFuture[L], right: CompletableFuture[R])
                                  (implicit prettifier: Prettifier, pos: Position): Assertion = {
    (Try(left.get(10, TimeUnit.SECONDS)), Try(right.get(10, TimeUnit.SECONDS))) match {
      case (Failure(l), Failure(r)) => assert(l.toString == r.toString) // some problems with equals checking Failure with concurrent ExecutionException
      case (l, r) => assert(l == r)
    }
  }
}
