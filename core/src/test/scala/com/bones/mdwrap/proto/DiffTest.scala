package com.bones.mdwrap.proto

import com.bones.mdwrap.{Column, DataType, DatabaseCache, DatabaseQuery, Nullable, Table, TableType, YesNo}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class DiffTest extends AnyFunSuite with Matchers {

  val tables = List(
    Table(None, Some("public"), "table1", Right(TableType.Table), None, None, None, None, None, None),
    Table(None, Some("public"), "table2", Right(TableType.Table), None, None, None, None, None, None)
  )

  val columns = List(
    Column(None, Some("public"), "table1", "id", DataType.Integer, "int8", 1000, None, 0, Nullable.ColumnNoNulls, None, Some("select id from somesequence"), 0, 0, YesNo.No, None, YesNo.Yes, YesNo.No),
    Column(None, Some("public"), "table1", "name", DataType.VarChar, "text", 1000, None, 0, Nullable.ColumnNoNulls, None, None, 0, 0, YesNo.No, None, YesNo.Yes, YesNo.No),
    Column(None, Some("public"), "table1", "age", DataType.Integer, "int8", 1000, None, 0, Nullable.ColumnNullable, None, None, 0, 0, YesNo.Yes, None, YesNo.Yes, YesNo.No),
    Column(None, Some("public"), "table2", "id", DataType.Integer, "int8", 1000, None, 0, Nullable.ColumnNoNulls, None, Some("select id from somesequence"), 0, 0, YesNo.No, None, YesNo.Yes, YesNo.No),
    Column(None, Some("public"), "table2", "table1_id", DataType.Integer, "text", 1000, None, 0, Nullable.ColumnNoNulls, None, None, 0, 0, YesNo.No, None, YesNo.Yes, YesNo.No),
    Column(None, Some("public"), "table2", "occupation", DataType.VarChar, "text", 1000, None, 0, Nullable.ColumnNoNulls, None, None, 0, 0, YesNo.No, None, YesNo.Yes, YesNo.No)
  )

  val databaseCache = DatabaseCache.apply(DatabaseQuery.everything, List.empty, tables, columns, List.empty, List.empty)

  val table1Columns = List(
    ProtoColumn("id", DataType.Integer, false, None),
    ProtoColumn("name", DataType.VarChar, false, None),
    ProtoColumn("age", DataType.Integer, true, None)
  )
  val table2Columns = List(
    ProtoColumn("id", DataType.Integer, false, None),
    ProtoColumn("table1_id", DataType.Integer, false, None),
    ProtoColumn("occupation", DataType.VarChar, false, None)
  )

  val table1 = ProtoTable("table1", table1Columns, List.empty, None)
  val table2 = ProtoTable("table2", table2Columns, List.empty, None)

  val schema = ProtoSchema("public", List(table1, table2))

  test("prototype is exact match") {



    val result = Diff.findDiff(databaseCache, schema)

    result._1.length mustEqual 0
    result._2.length mustEqual 0
    result._3.length mustEqual 0
    result._4.length mustEqual 0
    result._5.length mustEqual 0

  }

}
