package com.bones.mdwrap.proto

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import Fixtures._

class DiffTest extends AnyFunSuite with Matchers {

  test("prototype is exact match") {

    val result = Diff.findDiff(databaseCache, schema)

    result._1.length mustEqual 0
    result._2.length mustEqual 0
//    result._3.length mustEqual 0
    result._4.length mustEqual 0
    result._5.length mustEqual 0

  }

}
