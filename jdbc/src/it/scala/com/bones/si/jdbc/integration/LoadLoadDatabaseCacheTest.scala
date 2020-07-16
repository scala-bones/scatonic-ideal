package com.bones.si.jdbc.integration

import java.sql.Connection

import com.bones.si.DatabaseQuery
import com.bones.si.jdbc.LoadDatabaseCache
import org.scalatest.matchers.must.Matchers

class LoadLoadDatabaseCacheTest extends IntegrationFixture with Matchers {

  test("load database") { f =>
    val query = DatabaseQuery.everything
    val cache = LoadDatabaseCache.load(query, List.empty, f.con)

    cache.tables.size must be > 0
    cache.columns.size must be > 0
    cache.primaryKeys.size must be > 0
    cache.crossReferences.size must be > 0
  }

}
