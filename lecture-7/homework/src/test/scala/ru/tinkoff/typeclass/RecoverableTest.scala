package ru.tinkoff.typeclass

import org.scalactic.Prettifier
import org.scalactic.source.Position
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import Recoverable._
import org.scalatest.Assertion

abstract class RecoverableTest[F[_]](implicit F: Recoverable[F]) extends AnyFunSuite with Matchers {

  def assertEqualsF[L, R](left: F[L], right: F[R])(implicit prettifier: Prettifier, pos: Position): Assertion

  test("'new'.map(identity)  <-> 'new' ") {
    assertEqualsF(F.`new`(10).map(identity), F.`new`(10))
  }

  test("fa.flatMap(f).flatMap(g) <-> fa.flatMap(a => f(a).flatMap(g))") {
    def f(i: Int) = F.`new`(i * 3)
    def g(i: Int) = F.`new`(i * 4)

    assertEqualsF(F.`new`(10).flatMap(f).flatMap(g), F.`new`(10).flatMap(i => f(i).flatMap(g)))
  }

  test("'new'.flatMap(error) <-> raiseError(error)") {
    val exception = new RuntimeException

    assertEqualsF(F.`new`(10).flatMap(_ => F.raiseError[Int](exception)), F.raiseError[Int](exception))
  }

  test("raiseError(e).recover(value) <-> 'new'") {
    assertEqualsF(F.raiseError[Int](new RuntimeException).recover { case _ => 10}, F.`new`(10))
  }

  test("raiseError(e).recoverWith('new') <-> 'new'") {
    assertEqualsF(F.raiseError[Int](new RuntimeException).recoverWith { case _ => F.`new`(10)}, F.`new`(10))
  }

  test("'new'.recoverWith(throw error) <-> 'new'") {
    assertEqualsF(F.`new`(10).recover { case _ => throw new RuntimeException }, F.`new`(10))
  }
}




