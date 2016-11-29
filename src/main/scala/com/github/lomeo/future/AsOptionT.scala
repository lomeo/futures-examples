package com.github.lomeo.future

import scala.language.higherKinds
import scala.language.implicitConversions

import cats.data.OptionT
import cats.{Functor, Monad}

import simulacrum.typeclass

@typeclass trait AsOptionT[F[+_]] {

    def toOptionT[O, T](f: F[O])(implicit evF: Functor[F], evO: O =:= Option[T]): OptionT[F, T] =
        OptionT(f.asInstanceOf[F[Option[T]]])

    def getOrElse[O, T](f: F[O], default: => T)(implicit evM: Monad[F], evO: O =:= Option[T]): F[T] =
        OptionT(f.asInstanceOf[F[Option[T]]]).getOrElse(default)

    def getOrElseF[O, T](f: F[O], default: => F[T])(implicit evM: Monad[F], evO: O =:= Option[T]): F[T] =
        OptionT(f.asInstanceOf[F[Option[T]]]).getOrElseF(default)
}
