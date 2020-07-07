package com.bones.mdwrap.jdbc

import scala.util.Try

class Borrow[T<% {def close() }](closable: () => T) {

  def borrow[A](f: T => Try[A]): Try[A] = for {
    c <- Try { closable() }
    result <- f.apply(c)
    _ <- Try { c.close() }
  } yield result
}
