package com.fortysevendeg.ninecards.api.utils

import cats._
import cats.free.Free
import shapeless.Lazy
import spray.httpx.marshalling.ToResponseMarshaller

import scala.language.{higherKinds, implicitConversions}
import scalaz.concurrent.Task

object TaskUtils {

  implicit def taskMonad = new Monad[Task] {
    override def flatMap[A, B](fa: Task[A])(f: A => Task[B]): Task[B] =
      fa.flatMap(f)

    override def pure[A](a: A): Task[A] = Task.now(a)
  }

  implicit def tasksMarshaller[A](
    implicit m: ToResponseMarshaller[A]): ToResponseMarshaller[Task[A]] =
    ToResponseMarshaller[Task[A]] {
      (task, ctx) =>
        task.runAsync {
          _.fold(
            left => ctx.handleError(left),
            right => m(right, ctx))
        }
    }

  implicit def freeTaskMarshaller[S[_], M[_], A](
    implicit int: S ~> Task,
    taskMarshaller: Lazy[ToResponseMarshaller[Task[A]]]): ToResponseMarshaller[Free[S, A]] =
    ToResponseMarshaller[Free[S, A]] {
      (free, ctx) =>
        taskMarshaller.value(free.foldMap(int), ctx)
    }
}
