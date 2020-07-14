package com.bones.mdwrap.jdbc.integration

import com.bones.mdwrap.{DatabaseQuery, ProcedureType}
import com.bones.mdwrap.jdbc.LoadProcedure
import org.scalatest.matchers.must.Matchers

class LoadProcedureTest extends IntegrationFixture with Matchers {

  test("load procedures") { f=>

    val query = DatabaseQuery.everything

    val procedures = LoadProcedure.load(query, f.con)

    procedures.length must be > 0

    val p = procedures.head
    p.catalogName mustEqual None
    p.schemaName mustEqual Some("public")
    p.name mustEqual "db_test_insert_data"
    p.remarks mustEqual None
    p.procedureType mustEqual ProcedureType.ProcedureReturnsResult
    p.specificName must startWith("db_test_insert_data_")

  }

}
