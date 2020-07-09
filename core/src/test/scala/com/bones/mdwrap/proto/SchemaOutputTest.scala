package com.bones.mdwrap.proto

import org.scalatest.funspec.AnyFunSpec
import Fixtures._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers


class SchemaOutputTest extends AnyFunSuite with Matchers {

  test("sql output for columns") {
    val output = SchemaOutput.postgresCreateColumnStatements("public", columnCreate.map( c => (table1, c)))

    val expectedResult = """alter table public.table1 add column binary_one bit;
                           |alter table public.table1 add column binary_hundo bytea;
                           |alter table public.table1 add column binary_unfixed bytea;
                           |alter table public.table1 add column boolean_col boolean;
                           |alter table public.table1 add column fixed_binary bytea;
                           |alter table public.table1 add column fixed_char character(100);
                           |alter table public.table1 add column date_col date;
                           |alter table public.table1 add column double_col double precision;
                           |alter table public.table1 add column integer_ser_col serial not null;
                           |alter table public.table1 add column integer_col integer;
                           |alter table public.table1 add column inverval_col interval;
                           |alter table public.table1 add column long_col bigint;
                           |alter table public.table1 add column long_auto bigserial;
                           |alter table public.table1 add column numeric_col numeric(10,2);
                           |alter table public.table1 add column real_col real;
                           |alter table public.table1 add column small_int_col smallint;
                           |alter table public.table1 add column time_col_with time with time zone;
                           |alter table public.table1 add column time_col_without time without time zone;
                           |alter table public.table1 add column timestamp_col_with time with time zone;
                           |alter table public.table1 add column timestamp_col_without time without time zone;""".stripMargin

    output mustEqual expectedResult
  }

  test("sql output for tables") {
    val output = SchemaOutput.postgresTableStatement("public", table1)
    output mustEqual "create table public.table1 (id serial not null, name text not null, age integer)"

    val output2 = SchemaOutput.postgresTableStatement("public", table2)
    output2 mustEqual "create table public.table2 (id serial not null, table1_id integer not null, occupation varchar(100) not null)"

    val output3 = SchemaOutput.postgresTableStatement("public", table3)
    output3 mustEqual
      """create table public.table3 (binary_one bit, binary_hundo bytea,
         |binary_unfixed bytea, boolean_col boolean, fixed_binary bytea, fixed_char character(100),
         |date_col date, double_col double precision, inverval_col interval, long_col bigint,
         |long_auto bigserial not null, numeric_col numeric(10,2), real_col real, small_int_col smallint,
         |time_col_with time with time zone, time_col_without time without time zone,
         |timestamp_col_with time with time zone, timestamp_col_without time without time zone)""".stripMargin.replaceAll("\n", " ")

  }

}
