package ru.tinkoff.typeclass

import org.scalactic.Prettifier
import org.scalactic.source.Position
import org.scalatest.Assertion

import scala.util.Try

class RecoverableTryTest extends RecoverableTest[Try] {
  override def assertEqualsF[L, R](left: Try[L], right: Try[R])
                                  (implicit prettifier: Prettifier, pos: Position): Assertion =
    assert(left == right)
}



