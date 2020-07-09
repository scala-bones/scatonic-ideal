package com.bones.mdwrap.proto

import java.nio.charset.{Charset, StandardCharsets}

import com.bones.mdwrap.{Column, DataType, DatabaseCache, DatabaseQuery, Nullable, Table, TableType, YesNo}

object Fixtures {
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
    ProtoColumn("id", IntegerType.autoIncrement, false, None),
    ProtoColumn("name", StringType(), false, None),
    ProtoColumn("age", IntegerType(), true, None)
  )
  val table2Columns = List(
    ProtoColumn("id", IntegerType.autoIncrement, false, None),
    ProtoColumn("table1_id", IntegerType(), false, None),
    ProtoColumn("occupation", StringType(100), false, None)
  )

  val table3Columns = List(
    ProtoColumn("binary_one", BinaryType(1), false, None),
    ProtoColumn("binary_hundo", BinaryType(100), false, None),
    ProtoColumn("binary_unfixed", BinaryType(), false, None),
    ProtoColumn("boolean_col", BooleanType, false, None),
    ProtoColumn("fixed_binary", FixedLengthBinaryType(100), false, None),
    ProtoColumn("fixed_char", FixedLengthCharacterType(100, StandardCharsets.UTF_8), false, None),
    ProtoColumn("date_col", DateType, false, None),
    ProtoColumn("double_col", DoubleType, false, None),
    ProtoColumn("inverval_col", IntervalType, false, None),
    ProtoColumn("long_col", LongType(), false, None),
    ProtoColumn("long_auto", LongType.autoIncrement, false, None),
    ProtoColumn("numeric_col", NumericType(10,2), false, None),
    ProtoColumn("real_col", RealType, false, None),
    ProtoColumn("small_int_col", SmallIntType, false, None),
    ProtoColumn("time_col_with", TimeType.withTimeZone(), false, None),
    ProtoColumn("time_col_without", TimeType.withoutTimeZone(), false, None),
    ProtoColumn("timestamp_col_with", TimestampType.withTimeZone(), false, None),
    ProtoColumn("timestamp_col_without", TimestampType.withoutTimeZone(), false, None)
  )

  val table1 = ProtoTable("table1", table1Columns, List.empty, None)
  val table2 = ProtoTable("table2", table2Columns, List.empty, None)
  val table3 = ProtoTable("table3", table3Columns, List.empty, None)

  val schema = ProtoSchema("public", List(table1, table2))
}
