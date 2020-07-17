package com.bones.si.jdbc.load.integration

import com.bones.si.jdbc.load.{DatabaseQuery, LoadDatabaseCache}
import org.scalatest.matchers.must.Matchers

class LoadLoadDatabaseMetadataCacheTest extends IntegrationFixture with Matchers {

  test("load database") { f =>
    val query = DatabaseQuery.everything
    val cache = LoadDatabaseCache.load(query, List.empty, f.con)

    cache.tables.size must be > 0
    cache.columns.size must be > 0
    cache.primaryKeys.size must be > 0
    cache.crossReferences.size must be > 0
  }

}
