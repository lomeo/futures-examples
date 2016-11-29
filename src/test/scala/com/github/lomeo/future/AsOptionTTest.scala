package com.github.lomeo.future

import scala.concurrent.Future

import cats.data.OptionT
import cats.instances.FutureInstances
import org.scalatest._


class AsOptionTTest extends AsyncFlatSpec with Matchers with FutureInstances {

    import AsOptionT.ops._

    implicit def asOptionTInstanceForFuture: AsOptionT[Future] = new AsOptionT[Future] {}

    behavior of "AsOptionT.getOrElse"

    it should "return original future if a future has a value" in {
        Future.successful(Option(42)).getOrElse(13).map(n => assert(n === 42))
    }

    it should "return default future if a future has not a value" in {
        Future.successful(Option.empty[Int]).getOrElse(13).map(n => assert(n === 13))
    }

    it should "pass exception" in {
        recoverToSucceededIf[IllegalStateException] {
            Future.failed[Option[Int]](new IllegalStateException).getOrElse(0)
        }
    }

    behavior of "AsOptionT.getOrElseF"

    it should "return original future if a future has a value" in {
        Future.successful(Option(42)).getOrElseF(Future.successful(13)).map(n => assert(n === 42))
    }

    it should "return default future if a future has not a value" in {
        Future.successful(Option.empty[Int]).getOrElseF(Future.successful(13)).map(n => assert(n === 13))
    }

    it should "pass exception" in {
        recoverToSucceededIf[IllegalStateException] {
            Future.failed[Option[Int]](new IllegalStateException).getOrElseF(Future.successful(0))
        }
    }
}
