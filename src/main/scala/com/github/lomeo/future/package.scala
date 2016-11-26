package com.github.lomeo

import scala.language.higherKinds
import scala.concurrent.{Future, ExecutionContext}


package object future {

    implicit sealed class FutureOption[T](future: Future[Option[T]]) {

        def getOrElse(default: => T)(implicit executor: ExecutionContext): Future[T] =
            future.map(_.getOrElse(default))

        def orElse(default: => Future[T])(implicit executor: ExecutionContext): Future[T] =
            future flatMap {
                case Some(x) => Future.successful(x)
                case None => default
            }
    }

    trait MyMonad[F[_]] {

        def pure[A](x: A): F[A]
        def map[A, B](m: F[A])(f: A => B): F[B]
        def flatMap[A, B](m: F[A])(f: A => F[B]): F[B]
    }

    class MyOptionT[F[_], A](val value: F[Option[A]]) {

        def map[B](f: A => B)(implicit m: MyMonad[F]): MyOptionT[F, B] =
            new MyOptionT(m.map(value)(_.map(f)))

        def flatMap[B](f: A => MyOptionT[F, B])(implicit m: MyMonad[F]): MyOptionT[F, B] =
            new MyOptionT(m.flatMap(value)(_.fold(m.pure(Option.empty[B]))(x => f(x).value)))

        def getOrElse(default: => A)(implicit m: MyMonad[F]): F[A] =
            m.map(value)(_.getOrElse(default))

        def orElse(default: => F[A])(implicit m: MyMonad[F]): F[A] =
            m.flatMap(value)(_.fold(default)(m.pure))
    }
}
