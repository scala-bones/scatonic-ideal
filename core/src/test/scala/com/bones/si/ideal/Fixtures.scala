package com.bones.si.ideal

import java.nio.charset.StandardCharsets

import com.bones.si.jdbc.load.{DatabaseMetadataCache, DatabaseQuery}
import com.bones.si.jdbc.{Column, DataType, Nullable, PrimaryKey, Table, TableType, YesNo}

object Fixtures {
  val tables = List(
    Table(
      None,
      Some("public"),
      "table1",
      Right(TableType.Table),
      None,
      None,
      None,
      None,
      None,
      None),
    Table(
      None,
      Some("public"),
      "table2",
      Right(TableType.Table),
      None,
      None,
      None,
      None,
      None,
      None)
  )

  val columns = List(
    Column(
      None,
      Some("public"),
      "table1",
      "id",
      DataType.Integer,
      "int8",
      1000,
      None,
      0,
      Nullable.ColumnNullable,
      None,
      Some("select id from somesequence"),
      0,
      0,
      YesNo.No,
      None,
      YesNo.Yes,
      YesNo.No
    ),
    Column(
      None,
      Some("public"),
      "table1",
      "name",
      DataType.VarChar,
      "text",
      1000,
      None,
      0,
      Nullable.ColumnNullable,
      None,
      None,
      0,
      0,
      YesNo.No,
      None,
      YesNo.Yes,
      YesNo.No),
    Column(
      None,
      Some("public"),
      "table1",
      "age",
      DataType.Integer,
      "int8",
      1000,
      None,
      0,
      Nullable.ColumnNullable,
      None,
      None,
      0,
      0,
      YesNo.Yes,
      None,
      YesNo.Yes,
      YesNo.No),
    Column(
      None,
      Some("public"),
      "table2",
      "id",
      DataType.Integer,
      "int8",
      1000,
      None,
      0,
      Nullable.ColumnNoNulls,
      None,
      Some("select id from somesequence"),
      0,
      0,
      YesNo.No,
      None,
      YesNo.Yes,
      YesNo.No
    ),
    Column(
      None,
      Some("public"),
      "table2",
      "table1_id",
      DataType.Integer,
      "text",
      1000,
      None,
      0,
      Nullable.ColumnNoNulls,
      None,
      None,
      0,
      0,
      YesNo.No,
      None,
      YesNo.Yes,
      YesNo.No),
    Column(
      None,
      Some("public"),
      "table2",
      "occupation",
      DataType.VarChar,
      "text",
      100,
      None,
      0,
      Nullable.ColumnNullable,
      None,
      None,
      0,
      0,
      YesNo.No,
      None,
      YesNo.Yes,
      YesNo.No)
  )

  val table1Pk = PrimaryKey(None, Some("public"), "table1", "id", 1, Some("table1_id_pk"))
  val table2Pk = PrimaryKey(None, Some("public"), "table2", "id", 1, Some("table2_id_pk"))
  val pks = List(table1Pk, table2Pk)

//  val crossRef = CrossReference(None, Some("public"), "table2", )

  val databaseCache = DatabaseMetadataCache.apply(
    DatabaseQuery.everything,
    List.empty,
    tables,
    columns,
    List.empty,
    pks)

  val table1Pks = List(IdealColumn("id", IntegerType.autoIncrement, false, None))
  val table1Columns = List(
    IdealColumn("name", StringType.unbounded, false, None),
    IdealColumn("age", IntegerType(), true, None)
  )
  val table2Pks = List(IdealColumn("id", IntegerType.autoIncrement, false, None))
  val table2Columns = List(
    IdealColumn("table1_id", IntegerType(), false, None),
    IdealColumn("occupation", StringType(100), false, None)
  )

  val table3Columns = List(
    IdealColumn("binary_one", BinaryType(1), true, None),
    IdealColumn("binary_hundo", BinaryType(100), true, None),
    IdealColumn("binary_unfixed", BinaryType.unbounded, true, None),
    IdealColumn("boolean_col", BooleanType, true, None),
    IdealColumn("fixed_binary", FixedLengthBinaryType(100), true, None),
    IdealColumn("fixed_char", FixedLengthCharacterType(100, StandardCharsets.UTF_8), true, None),
    IdealColumn("date_col", DateType, true, None),
    IdealColumn("double_col", DoubleType, true, None),
    IdealColumn("inverval_col", IntervalType, true, None),
    IdealColumn("long_col", LongType(), true, None),
    IdealColumn("long_auto", LongType.autoIncrement, false, None),
    IdealColumn("numeric_col", NumericType(10, 2), true, None),
    IdealColumn("real_col", RealType, true, None),
    IdealColumn("small_int_col", SmallIntType, true, None),
    IdealColumn("time_col_with", TimeType.withTimeZone(), true, None),
    IdealColumn("time_col_without", TimeType.withoutTimeZone(), true, None),
    IdealColumn("timestamp_col_with", TimestampType.withTimeZone(), true, None),
    IdealColumn("timestamp_col_without", TimestampType.withoutTimeZone(), true, None)
  )

  val table1 = IdealTable("table1", table1Pks, table1Columns, List.empty, None)
  val table2 = IdealTable("table2", table2Pks, table2Columns, List.empty, None)
  val table3 = IdealTable("table3", List.empty, table3Columns, List.empty, None)

  val schema = IdealSchema("public", List(table1, table2))

  val columnCreate = List(
    IdealColumn("binary_one", BinaryType(1), true, None),
    IdealColumn("binary_hundo", BinaryType(100), true, None),
    IdealColumn("binary_unfixed", BinaryType.unbounded, true, None),
    IdealColumn("boolean_col", BooleanType, true, None),
    IdealColumn("fixed_binary", FixedLengthBinaryType(100), true, None),
    IdealColumn("fixed_char", FixedLengthCharacterType(100, StandardCharsets.UTF_8), true, None),
    IdealColumn("date_col", DateType, true, None),
    IdealColumn("double_col", DoubleType, true, None),
    IdealColumn("integer_ser_col", IntegerType.autoIncrement, false, None),
    IdealColumn("integer_col", IntegerType(), true, None),
    IdealColumn("inverval_col", IntervalType, true, None),
    IdealColumn("long_col", LongType(), true, None),
    IdealColumn("long_auto", LongType.autoIncrement, true, None),
    IdealColumn("numeric_col", NumericType(10, 2), true, None),
    IdealColumn("real_col", RealType, true, None),
    IdealColumn("small_int_col", SmallIntType, true, None),
    IdealColumn("time_col_with", TimeType.withTimeZone(), true, None),
    IdealColumn("time_col_without", TimeType.withoutTimeZone(), true, None),
    IdealColumn("timestamp_col_with", TimestampType.withTimeZone(), true, None),
    IdealColumn("timestamp_col_without", TimestampType.withoutTimeZone(), true, None)
  )
}
