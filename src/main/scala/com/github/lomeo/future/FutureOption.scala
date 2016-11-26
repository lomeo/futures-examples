package com.github.lomeo

import scala.concurrent.{Future, ExecutionContext}


package object future {

    implicit sealed class FutureOption[T](future: Future[Option[T]]) {

        def getOrElse(default: => T)(implicit executor: ExecutionContext): Future[T] = future.map(_.getOrElse(default))

        def orElse(default: => Future[T])(implicit executor: ExecutionContext): Future[T] = future flatMap {
            case Some(x) => Future(x)
            case None => default
        }
    }
}
