package com.bones.mdwrap.jdbc.integration

import java.sql.Connection

import com.bones.mdwrap.DatabaseQuery
import com.bones.mdwrap.jdbc.LoadPrimaryKey
import org.scalatest.matchers.must.Matchers

class LoadPrimaryKeyTest extends IntegrationFixture with Matchers {

  test("load primary key") { f =>

    val query = DatabaseQuery.everything

    val primaryKey = LoadPrimaryKey.load(query, f.con)

    val a = primaryKey.filter(_.tableName == "wrapper_table_a")
    val b = primaryKey.filter(_.tableName == "wrapper_table_b")

    a(0).catalogName mustEqual None
    a(0).schemaName mustEqual Some("public")
    a(0).tableName mustEqual "wrapper_table_a"
    a(0).columnName mustEqual "id"
    a(0).keySequence mustEqual 1
    a(0).name mustEqual Some("wrapper_table_a_pkey")

    a(1).columnName mustEqual "big_id"
    a(1).keySequence mustEqual 2

    b(0).catalogName mustEqual None
    b(0).schemaName mustEqual Some("public")
    b(0).tableName mustEqual "wrapper_table_b"
    b(0).columnName mustEqual "id"
    b(0).keySequence mustEqual 1
    b(0).name mustEqual Some("wrapper_table_b_pkey")



  }
}
