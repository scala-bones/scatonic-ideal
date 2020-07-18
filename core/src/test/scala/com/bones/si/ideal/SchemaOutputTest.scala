package com.bones.si.ideal

import com.bones.si.ideal.Fixtures._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers


class SchemaOutputTest extends AnyFunSuite with Matchers {

  test("sql output for columns lowercase") {
    val schemaOutput = PostgresSqlOutput.lowercase
    val output = schemaOutput.addColumnOutput("public", columnCreate.map(c => (table1, c)))

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
                           |alter table public.table1 add column timestamp_col_with timestamp with time zone;
                           |alter table public.table1 add column timestamp_col_without timestamp without time zone;""".stripMargin

    output.mkString("", ";\n", ";") mustEqual expectedResult
  }

  test("sql output for columns uppercase") {
    val schemaOutput = PostgresSqlOutput.uppercase
    val output = schemaOutput.addColumnOutput("public", columnCreate.map(c => (table1, c)))

    println(output.mkString("\n"))
    val expectedResult = """ALTER TABLE public.table1 ADD COLUMN binary_one BIT;
                           |ALTER TABLE public.table1 ADD COLUMN binary_hundo BYTEA;
                           |ALTER TABLE public.table1 ADD COLUMN binary_unfixed BYTEA;
                           |ALTER TABLE public.table1 ADD COLUMN boolean_col BOOLEAN;
                           |ALTER TABLE public.table1 ADD COLUMN fixed_binary BYTEA;
                           |ALTER TABLE public.table1 ADD COLUMN fixed_char CHARACTER(100);
                           |ALTER TABLE public.table1 ADD COLUMN date_col DATE;
                           |ALTER TABLE public.table1 ADD COLUMN double_col DOUBLE PRECISION;
                           |ALTER TABLE public.table1 ADD COLUMN integer_ser_col SERIAL NOT NULL;
                           |ALTER TABLE public.table1 ADD COLUMN integer_col INTEGER;
                           |ALTER TABLE public.table1 ADD COLUMN inverval_col INTERVAL;
                           |ALTER TABLE public.table1 ADD COLUMN long_col BIGINT;
                           |ALTER TABLE public.table1 ADD COLUMN long_auto BIGSERIAL;
                           |ALTER TABLE public.table1 ADD COLUMN numeric_col NUMERIC(10,2);
                           |ALTER TABLE public.table1 ADD COLUMN real_col REAL;
                           |ALTER TABLE public.table1 ADD COLUMN small_int_col SMALLINT;
                           |ALTER TABLE public.table1 ADD COLUMN time_col_with TIME WITH TIME ZONE;
                           |ALTER TABLE public.table1 ADD COLUMN time_col_without TIME WITHOUT TIME ZONE;
                           |ALTER TABLE public.table1 ADD COLUMN timestamp_col_with TIMESTAMP WITH TIME ZONE;
                           |ALTER TABLE public.table1 ADD COLUMN timestamp_col_without TIMESTAMP WITHOUT TIME ZONE;""".stripMargin

    output.mkString("", ";\n", ";") mustEqual expectedResult
  }

  test("sql output for tables lowercase") {
    val schemaOutput = PostgresSqlOutput.lowercase
    val output = schemaOutput.createTableOutput("public", table1)
    output.head mustEqual "create table public.table1 (id serial not null primary key, name text not null, age integer)"

    val output2 = schemaOutput.createTableOutput("public", table2)
    output2.head mustEqual "create table public.table2 (id serial not null primary key, table1_id integer not null, occupation varchar(100) not null)"

    val output3 = schemaOutput.createTableOutput("public", table3)
    output3.head mustEqual
      """create table public.table3 (binary_one bit, binary_hundo bytea,
         |binary_unfixed bytea, boolean_col boolean, fixed_binary bytea, fixed_char character(100),
         |date_col date, double_col double precision, inverval_col interval, long_col bigint,
         |long_auto bigserial not null, numeric_col numeric(10,2), real_col real, small_int_col smallint,
         |time_col_with time with time zone, time_col_without time without time zone,
         |timestamp_col_with timestamp with time zone, timestamp_col_without timestamp without time zone)""".stripMargin.replaceAll("\n", " ")

  }

  test("sql output for tables uppercase") {
    val schemaOutput = PostgresSqlOutput.uppercase
    val output = schemaOutput.createTableOutput("public", table1)
    output.head mustEqual "CREATE TABLE public.table1 (id SERIAL NOT NULL PRIMARY KEY, name TEXT NOT NULL, age INTEGER)"

    val output2 = schemaOutput.createTableOutput("public", table2)
    output2.head mustEqual "CREATE TABLE public.table2 (id SERIAL NOT NULL PRIMARY KEY, table1_id INTEGER NOT NULL, occupation VARCHAR(100) NOT NULL)"

    val output3 = schemaOutput.createTableOutput("public", table3)
    output3.head mustEqual
      """CREATE TABLE public.table3 (binary_one BIT, binary_hundo BYTEA,
        |binary_unfixed BYTEA, boolean_col BOOLEAN, fixed_binary BYTEA, fixed_char CHARACTER(100),
        |date_col DATE, double_col DOUBLE PRECISION, inverval_col INTERVAL, long_col BIGINT,
        |long_auto BIGSERIAL NOT NULL, numeric_col NUMERIC(10,2), real_col REAL, small_int_col SMALLINT,
        |time_col_with TIME WITH TIME ZONE, time_col_without TIME WITHOUT TIME ZONE,
        |timestamp_col_with TIMESTAMP WITH TIME ZONE, timestamp_col_without TIMESTAMP WITHOUT TIME ZONE)""".stripMargin.replaceAll("\n", " ")

  }

  test("multiple primary keys lowercase") {
    val schemaOutput = PostgresSqlOutput.lowercase
    val table = table1.copy(primaryKeyColumns = table1.primaryKeyColumns :+ IdealColumn("other_id", LongType(), false, None))
    val output = schemaOutput.createTableOutput("public", table)
    output.head mustEqual "create table public.table1 (id serial not null, other_id bigint not null, name text not null, age integer, primary key (id, other_id))"
  }

  test("multiple primary keys uppercase") {
    val schemaOutput = PostgresSqlOutput.uppercase
    val table = table1.copy(primaryKeyColumns = table1.primaryKeyColumns :+ IdealColumn("other_id", LongType(), false, None))
    val output = schemaOutput.createTableOutput("public", table)
    output.head mustEqual "CREATE TABLE public.table1 (id SERIAL NOT NULL, other_id BIGINT NOT NULL, name TEXT NOT NULL, age INTEGER, PRIMARY KEY (id, other_id))"
  }


}
