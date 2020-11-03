package com.bones.si.ideal

import java.nio.charset.StandardCharsets

import com.bones.si.ideal.Diff.{ColumnDataTypeDiff, ColumnNullableDiff}
import com.bones.si.ideal.Fixtures._
import com.bones.si.jdbc.{DataType, YesNo}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

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
    val newColumn = IdealColumn("new_col", IntegerType(), true, None)
    val otherNewColumn = IdealColumn("other_new", StringType.unbounded, true, None)
    val tableA = table2.copy(columns = newColumn :: table2.columns)
    val tableB = table1.copy(columns = otherNewColumn :: table1.columns)
    val s = IdealSchema("public", List(tableA, tableB))
    val result = Diff.findDiff(databaseCache, s)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing mustEqual List((tableA, newColumn), (tableB, otherNewColumn))
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0
  }

  test("report column difference") {

    val newNotNullable = IdealColumn("age", IntegerType(), false, None)
    val newShorterString = IdealColumn("name", StringType(100), false, None)
    val tableColumnsChanged = List(
      IdealColumn("id", IntegerType.autoIncrement, false, None),
      newShorterString,
      newNotNullable
    )

    val newTable1 =
      IdealTable("table1", table1Pks, tableColumnsChanged, List.empty, List.empty, None)
    val newTable2 = IdealTable("table2", table2Pks, table2Columns, List.empty, List.empty, None)

    val newSchema = IdealSchema("public", List(newTable1, newTable2))

    val result = Diff.findDiff(databaseCache, newSchema)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing.length mustEqual 0
    result.columnsDifferent.length mustEqual 2
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0

    result.columnsDifferent(0) mustEqual (newTable1, newNotNullable, List(
      ColumnNullableDiff(YesNo.Yes, YesNo.No)
    ))
    result.columnsDifferent(1) mustEqual (newTable1, newShorterString, List(
      ColumnDataTypeDiff(DataType.VarChar, StringType(Some(100), StandardCharsets.UTF_8))
    ))

  }

  test("report missing primary keys") {
    val anotherPk = IdealColumn("other_id", IntegerType(), false, None)
    val table =
      IdealTable("table1", anotherPk :: table1Pks, table1Columns, List.empty, List.empty, None)

    val schema = IdealSchema("public", List(table, table2))

    val result = Diff.findDiff(databaseCache, schema)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing.length mustEqual 1
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 1
    result.primaryKeysExtraneous.length mustEqual 0
    result.missingForeignKeys.length mustEqual 0

    result.columnsMissing(0)._1 mustEqual table
    result.columnsMissing(0)._2 mustEqual anotherPk
    result.primaryKeysMissing mustEqual List((table, anotherPk))
  }

  test("report missing extraneous primary keys") {

    // move primary key to be a regular column, it should be reported as a missing PK.
    val table =
      IdealTable("table1", List.empty, table1Pks ::: table1Columns, List.empty, List.empty, None)
    val schema = IdealSchema("public", List(table, table2))

    val result = Diff.findDiff(databaseCache, schema)

    result.tablesMissing.length mustEqual 0
    result.columnsMissing.length mustEqual 0
    result.columnsDifferent.length mustEqual 0
    result.primaryKeysMissing.length mustEqual 0
    result.primaryKeysExtraneous.length mustEqual 1
    result.missingForeignKeys.length mustEqual 0

    result.primaryKeysExtraneous(0) mustEqual (table, table1Pk)

  }

  test("") {}

}
