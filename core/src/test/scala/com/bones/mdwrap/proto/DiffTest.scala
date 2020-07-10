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

    result._1.length mustEqual 0
    result._2.length mustEqual 0
    result._3.length mustEqual 0
    result._4.length mustEqual 0
    result._5.length mustEqual 0

  }

  test("report missing table") {
    val result = Diff.findDiff(databaseCache, schema.copy(tables = table3 :: schema.tables))
    result._1 mustEqual List(table3)
    result._2.length mustEqual 0
    result._3.length mustEqual 0
    result._4.length mustEqual 0
    result._5.length mustEqual 0
  }

  test("report missing columns") {
    val newColumn = ProtoColumn("new_col", IntegerType(), true, None)
    val otherNewColumn = ProtoColumn("other_new", StringType(), true, None)
    val tableA = table2.copy(columns =  newColumn :: table2.columns)
    val tableB = table1.copy(columns = otherNewColumn :: table1.columns)
    val s = ProtoSchema("public", List(table1, tableA))
    val result = Diff.findDiff(databaseCache, s)
    result._1.length mustEqual 0
    result._2 mustEqual List( (tableB, otherNewColumn), (tableA, newColumn))
    result._3.length mustEqual 0
    result._4.length mustEqual 0
    result._5.length mustEqual 0
  }

  test("report column difference") {

    val newNotNullable = ProtoColumn("age", IntegerType(), false, None)
    val newShorterString = ProtoColumn("name", StringType(100), false, None)
    val tableColumnsChanged = List(
      ProtoColumn("id", IntegerType.autoIncrement, false, None),
      newShorterString,
      newNotNullable
    )

    val newTable1 = ProtoTable("table1", tableColumnsChanged, List.empty, None)
    val newTable2 = ProtoTable("table2", table2Columns, List.empty, None)

    val newSchema = ProtoSchema("public", List(newTable1, newTable2))

    val result = Diff.findDiff(databaseCache, newSchema)
    result._1.length mustEqual 0
    result._2.length mustEqual 0
    result._3.length mustEqual 2
    result._4.length mustEqual 0
    result._5.length mustEqual 0

    result._3(0) mustEqual (newNotNullable, List(ColumnNullableDiff(YesNo.Yes, YesNo.No)))
    result._3(1) mustEqual (newShorterString, List(ColumnDataTypeDiff(DataType.VarChar, StringType(Some(100), StandardCharsets.UTF_8))))

  }

}
