package com.bones.mdwrap.jdbc.integration

import com.bones.mdwrap.DatabaseQuery
import com.bones.mdwrap.jdbc.{Borrow, LoadAttribute}
import org.scalatest.matchers.must.Matchers

class LoadAttributeTest extends IntegrationFixture with Matchers {

  test("load attribute") { f =>
    val borrow = new Borrow(f.con)
    val query = DatabaseQuery.everything
    val attributes = LoadAttribute.load(query, borrow).get

    attributes.length must be > 0

  }

}
