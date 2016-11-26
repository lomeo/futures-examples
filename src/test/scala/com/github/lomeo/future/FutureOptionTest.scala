package com.github.lomeo.future

import scala.concurrent.Future
import org.scalatest._


class FutureOptionTest extends AsyncFlatSpec with Matchers {

    behavior of "FutureOption.getOrElse"

    it should "return value wrapped with Some" in {
        Future(Some(42)).getOrElse(0).map(n => assert(n === 42))
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
        Future(Some(42)).orElse(Future(0)).map(n => assert(n === 42))
    }

    it should "return default value if it evaluates to None" in {
        Future(Option.empty[Int]).orElse(Future(0)).map(n => assert(n === 0))
    }

    it should "fail if original future is failed" in {
        recoverToSucceededIf[IllegalStateException] {
            Future[Option[Int]](throw new IllegalStateException).orElse(Future(0))
        }
    }
}
