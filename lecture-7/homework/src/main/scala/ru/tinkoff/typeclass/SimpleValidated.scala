package ru.tinkoff.typeclass


sealed trait Validated[+A]

object Validated {
  def valid[A](a: A): Validated[A] = Valid(a)

  def invalid[A](e: Exception): Validated[A] = Invalid(List(e))
}

case class Invalid(error: List[Exception]) extends Validated[Nothing]

case class Valid[+A](value: A) extends Validated[A]


trait ParMappable[F[_]] {
  def map2[A, B, C](a: F[A], b: F[B])(f: (A, B) => C): F[C]

  def `new`[A](a: A): F[A]
}

object ParMappable {
  def apply[F[_]](implicit parMappable: ParMappable[F]): ParMappable[F] = parMappable

  implicit def validatedParMappable: ParMappable[Validated] = new ParMappable[Validated] {
    override def map2[A, B, C](a: Validated[A], b: Validated[B])(f: (A, B) => C): Validated[C] = (a, b) match {
      case (Valid(aa), Valid(bb)) => Valid(f(aa, bb))
      case (Invalid(e), Valid(_)) => Invalid(e)
      case (Valid(_), Invalid(e)) => Invalid(e)
      case (Invalid(e1), Invalid(e2)) => Invalid(e1 ::: e2)
    }

    override def `new`[A](a: A): Validated[A] = Valid(a)
  }

  implicit class cartesianMapN[A, B, F[_]](val ab: (F[A], F[B])) extends AnyVal {
    def mapN[C](f: (A, B) => C)(implicit F: ParMappable[F]): F[C] = F.map2(ab._1, ab._2)(f)
  }

}
