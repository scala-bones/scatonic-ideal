package com.bones.mdwrap.proto

import org.scalatest.funspec.AnyFunSpec
import Fixtures._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers


class SchemaUpdateTest extends AnyFunSuite with Matchers {

  test("sql output for tables") {
    val output = SchemaUpdate.toPostgresMigration("public", table1)
    output mustEqual "create table public.table1 (id serial, name text, age integer)"

    val output2 = SchemaUpdate.toPostgresMigration("public", table2)
    output2 mustEqual "create table public.table2 (id serial, table1_id integer, occupation varchar(100))"

    val output3 = SchemaUpdate.toPostgresMigration("public", table3)
    output3 mustEqual
      """create table public.table3 (binary_one bit, binary_hundo bytea,
         |binary_unfixed bytea, boolean_col boolean, fixed_binary bytea, fixed_char character(100),
         |date_col date, double_col double precision, inverval_col interval, long_col bigint,
         |long_auto bigserial, numeric_col numeric(10,2), real_col real, small_int_col smallint,
         |time_col_with time with time zone, time_col_without time without time zone,
         |timestamp_col_with time with time zone, timestamp_col_without time without time zone)""".stripMargin.replaceAll("\n", " ")

  }

}
