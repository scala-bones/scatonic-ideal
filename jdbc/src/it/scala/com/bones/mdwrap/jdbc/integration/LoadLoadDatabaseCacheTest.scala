package com.bones.mdwrap.jdbc.integration

import java.sql.Connection

import com.bones.mdwrap.DatabaseQuery
import com.bones.mdwrap.jdbc.{Borrow, LoadDatabaseCache}
import org.scalatest.matchers.must.Matchers

class LoadLoadDatabaseCacheTest extends IntegrationFixture with Matchers {

  test("load database") { f =>
    val query = DatabaseQuery.everything
    val borrow = new Borrow[Connection](f.con)
    val cache = LoadDatabaseCache.load(query, List.empty, borrow).get

    cache.tables.size must be > 0
    cache.columns.size must be > 0
    cache.primaryKeys.size must be > 0
    cache.crossReferences.size must be > 0
  }

}
