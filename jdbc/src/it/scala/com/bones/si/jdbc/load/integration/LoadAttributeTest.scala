package com.bones.si.jdbc.load.integration

import com.bones.si.jdbc.load.{DatabaseQuery, LoadAttribute}
import org.scalatest.matchers.must.Matchers

class LoadAttributeTest extends IntegrationFixture with Matchers {

  // ignore, load attribute is not supported by postgres
  ignore("load attribute") { f =>
    val query = DatabaseQuery.everything
    val attributes = LoadAttribute.load(query, f.con)

    attributes.length must be > 0

  }

}
