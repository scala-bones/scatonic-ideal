package com.bones.mdwrap.jdbc.integration

import java.sql.Connection

import com.bones.mdwrap.{DatabaseQuery, FunctionType}
import com.bones.mdwrap.jdbc.LoadFunction
import org.scalatest.matchers.must.Matchers

class LoadFunctionTest extends IntegrationFixture with Matchers {

  test ("can load functions" ) { f=>
    val query = DatabaseQuery.everything
    val functions = LoadFunction.load(query, f.con)

    val function = functions.filter(_.functionName == "db_test_add").head

    function.catalogName mustEqual Some("postgres")
    function.schemaName mustEqual Some("public")
    function.functionName mustEqual "db_test_add"
    function.remarks mustEqual None
    function.functionType mustEqual FunctionType.FunctionNoTable
    function.specificName must startWith("db_test_add_")

  }

}
