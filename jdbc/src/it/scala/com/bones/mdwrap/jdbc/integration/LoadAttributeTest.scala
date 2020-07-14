package com.bones.mdwrap.jdbc.integration

import com.bones.mdwrap.DatabaseQuery
import com.bones.mdwrap.jdbc.LoadAttribute
import org.scalatest.matchers.must.Matchers

class LoadAttributeTest extends IntegrationFixture with Matchers {

  // ignore, load attribute is not supported by postgres
  ignore("load attribute") { f =>
    val query = DatabaseQuery.everything
    val attributes = LoadAttribute.load(query, f.con)

    attributes.length must be > 0

  }

}
