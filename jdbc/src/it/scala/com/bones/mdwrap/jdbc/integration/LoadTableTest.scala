package com.bones.mdwrap.jdbc.integration

import com.bones.mdwrap.{DatabaseQuery, TableType}
import com.bones.mdwrap.jdbc.{Borrow, LoadTable}
import org.scalatest.matchers.must.Matchers

class LoadTableTest extends IntegrationFixture with Matchers {

  test("load public tables") { f =>
    val query = DatabaseQuery.everything.schemas("public")

    val borrow = new Borrow(f.con)

    val allTypes = LoadTable.loadTypes(query, borrow, TableType.Table)

    val tables = allTypes.get.filter(_.tableType == Right(TableType.Table))

    tables(0).catalogName mustEqual None
    tables(0).schemaName mustEqual Some("public")
    tables(0).name mustEqual "wrapper_table_a"
    tables(0).tableType mustEqual Right(TableType.Table)
    tables(0).remarks mustEqual None
    tables(0).typesSchema mustEqual None
    tables(0).typeName mustEqual None
    tables(0).selfReferencingColumnName mustEqual None
    tables(0).selfReferencingColumnName mustEqual None


  }

}
