package com.bones.si.jdbc.integration

import com.bones.si.{DatabaseQuery, TableType}
import com.bones.si.jdbc.LoadTable
import org.scalatest.matchers.must.Matchers

class LoadTableTest extends IntegrationFixture with Matchers {

  test("load public tables") { f =>
    val query = DatabaseQuery.everything.schemas("public")

    val allTypes = LoadTable.loadTypes(query, f.con, TableType.Table)

    val tables = allTypes.filter(_.tableType == Right(TableType.Table)).find(_.name == "wrapper_table_a").head

    tables.catalogName mustEqual None
    tables.schemaName mustEqual Some("public")
    tables.name mustEqual "wrapper_table_a"
    tables.tableType mustEqual Right(TableType.Table)
    tables.remarks mustEqual None
    tables.typesSchema mustEqual None
    tables.typeName mustEqual None
    tables.selfReferencingColumnName mustEqual None
    tables.selfReferencingColumnName mustEqual None


  }

}
