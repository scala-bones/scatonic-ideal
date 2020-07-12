package com.bones.mdwrap.proto

import java.nio.charset.StandardCharsets

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import Fixtures._
import com.bones.mdwrap.{DataType, YesNo}
import com.bones.mdwrap.proto.Diff.{ColumnDataTypeDiff, ColumnNullableDiff}

class DiffTest extends AnyFunSuite with Matchers {

  test("prototype is exact match") {

    val result = Diff.findDiff(databaseCache, schema)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing.length mustEqual 0
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0

  }

  test("report missing table") {
    val result = Diff.findDiff(databaseCache, schema.copy(tables = table3 :: schema.tables))
    result.tablesMissing mustEqual List(table3)
    result.columnsMissing.length mustEqual 0
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0
  }

  test("report missing columns") {
    val newColumn = ProtoColumn("new_col", IntegerType(), true, None)
    val otherNewColumn = ProtoColumn("other_new", StringType(), true, None)
    val tableA = table2.copy(columns =  newColumn :: table2.columns)
    val tableB = table1.copy(columns = otherNewColumn :: table1.columns)
    val s = ProtoSchema("public", List(tableA, tableB))
    val result = Diff.findDiff(databaseCache, s)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing mustEqual List( (tableA, newColumn), (tableB, otherNewColumn))
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0
  }

  test("report column difference") {

    val newNotNullable = ProtoColumn("age", IntegerType(), false, None)
    val newShorterString = ProtoColumn("name", StringType(100), false, None)
    val tableColumnsChanged = List(
      ProtoColumn("id", IntegerType.autoIncrement, false, None),
      newShorterString,
      newNotNullable
    )

    val newTable1 = ProtoTable("table1", tableColumnsChanged, table1Pks, List.empty, None)
    val newTable2 = ProtoTable("table2", table2Columns, table2Pks, List.empty, None)

    val newSchema = ProtoSchema("public", List(newTable1, newTable2))

    val result = Diff.findDiff(databaseCache, newSchema)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing.length mustEqual 0
    result.columnsDifferent.length mustEqual 2
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0

    result.columnsDifferent(0) mustEqual (newNotNullable, List(ColumnNullableDiff(YesNo.Yes, YesNo.No)))
    result.columnsDifferent(1) mustEqual (newShorterString, List(ColumnDataTypeDiff(DataType.VarChar, StringType(Some(100), StandardCharsets.UTF_8))))

  }

  test("report missing primary keys") {
    val anotherPk = ProtoColumn("other_id", IntegerType(), false, None)
    val table = ProtoTable("table1", table1Columns, anotherPk :: table1Pks, List.empty, None)

    val schema = ProtoSchema("public", List(table, table2))

    val result = Diff.findDiff(databaseCache, schema)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing.length mustEqual 1
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 1
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0

    result.columnsMissing(0)._1 mustEqual table
    result.columnsMissing(0)._2 mustEqual anotherPk
    result.primaryKeysMissing mustEqual List( (table, anotherPk) )
  }

  test("report missing extraneous primary keys") {

    // move primary key to be a regular column, it should be reported as a missing PK.
    val table = ProtoTable("table1", table1Pks ::: table1Columns, List.empty, List.empty, None)
    val schema = ProtoSchema("public", List(table, table2))

    val result = Diff.findDiff(databaseCache, schema)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing.length mustEqual 0
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 1
    result.missingForeignKeys.length mustEqual 0

    result.primaryKeysExtraneous(0) mustEqual (table, table1Pk)

  }

}
