package com.github.lomeo.future

import scala.concurrent.Future

import cats.data.OptionT
import cats.instances.FutureInstances
import org.scalatest._


class FutureOptionTest extends AsyncFlatSpec with Matchers with FutureInstances {

    import implicits._

    behavior of "FutureOption.getOrElse"

    it should "return value wrapped with Some" in {
        Future(Option(42)).getOrElse(0).map(n => assert(n === 42))
    }

    it should "return default value if it evaluates to None" in {
        Future(Option.empty[Int]).getOrElse(0).map(n => assert(n === 0))
    }

    it should "fail if original future is failed" in {
        recoverToSucceededIf[IllegalStateException] {
            Future[Option[Int]](throw new IllegalStateException).getOrElse(0)
        }
    }

    behavior of "FutureOption.orElse"

    it should "return value wrapped with Some" in {
        Future(Option(42)).orElse(Future(0)).map(n => assert(n === 42))
    }

    it should "return default value if it evaluates to None" in {
        Future(Option.empty[Int]).orElse(Future(0)).map(n => assert(n === 0))
    }

    it should "fail if original future is failed" in {
        recoverToSucceededIf[IllegalStateException] {
            Future[Option[Int]](throw new IllegalStateException).orElse(Future(0))
        }
    }

    behavior of "OptionT"

    it should "return value wrapped with Some" in {
        OptionT(Future(Option(42))).getOrElse(0).map(n => assert(n === 42))
        OptionT(Future(Option(42))).getOrElseF(Future(0)).map(n => assert(n === 42))
    }

    it should "return default value if it evaluates to None" in {
        OptionT(Future(Option.empty[Int])).getOrElse(0).map(n => assert(n === 0))
        OptionT(Future(Option.empty[Int])).getOrElseF(Future(0)).map(n => assert(n === 0))
    }

    it should "fail if original future is failed" in {
        recoverToSucceededIf[IllegalStateException] {
            OptionT(Future[Option[Int]](throw new IllegalStateException)).getOrElse(0)
        }
        recoverToSucceededIf[IllegalStateException] {
            OptionT(Future[Option[Int]](throw new IllegalStateException)).getOrElseF(Future(0))
        }
    }

    behavior of "MyOptionT"

    implicit val futureMonad = new MyMonad[Future] {
        def pure[A](x: A): Future[A] =
            Future.successful(x)

        def map[A, B](m: Future[A])(f: A => B): Future[B] =
            m.map(f)

        def flatMap[A, B](m: Future[A])(f: A => Future[B]): Future[B] =
            m.flatMap(f)
    }

    it should "return value wrapped with Some" in {
        new MyOptionT(Future(Option(42))).getOrElse(0).map(n => assert(n === 42))
        new MyOptionT(Future(Option(42))).orElse(Future(0)).map(n => assert(n === 42))
    }

    it should "return default value if it evaluates to None" in {
        new MyOptionT(Future(Option.empty[Int])).getOrElse(0).map(n => assert(n === 0))
        new MyOptionT(Future(Option.empty[Int])).orElse(Future(0)).map(n => assert(n === 0))
    }

    it should "fail if original future is failed" in {
        recoverToSucceededIf[IllegalStateException] {
            new MyOptionT(Future[Option[Int]](throw new IllegalStateException)).getOrElse(0)
        }
        recoverToSucceededIf[IllegalStateException] {
            new MyOptionT(Future[Option[Int]](throw new IllegalStateException)).orElse(Future(0))
        }
    }
}
