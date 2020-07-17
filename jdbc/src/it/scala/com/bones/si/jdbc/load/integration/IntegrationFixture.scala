package com.bones.si.jdbc.load.integration

import java.sql.Connection

import org.postgresql.ds.PGSimpleDataSource
import org.scalatest.funsuite.FixtureAnyFunSuite
import org.scalatest.{FixtureTestSuite, Outcome}

abstract class IntegrationFixture extends FixtureAnyFunSuite {

  case class FixtureParam(con: Connection)

  override def withFixture(test: OneArgTest): Outcome = {

    val dbUrl = Option(System.getProperty("pg-url"))
      .getOrElse("jdbc:postgresql://localhost/postgres?user=travis&password=secret")

    val ds = new PGSimpleDataSource() ;
    ds.setURL(dbUrl)
    val con = ds.getConnection
    dropTables(con)
    createStructures(con)
    val theFixture = FixtureParam(con)
    try {
      withFixture(test.toNoArgTest(theFixture))
    } finally {
      dropTables(ds.getConnection)
    }
  }

  private def createFunction(con: Connection): Unit = {
    val sql =
      """
        |CREATE OR REPLACE FUNCTION db_test_add(i1 integer, i2 integer) RETURNS integer
        |    AS 'select i1 + i2;'
        |    LANGUAGE SQL
        |    IMMUTABLE
        |    RETURNS NULL ON NULL INPUT;
        |""".stripMargin

    val st1 = con.createStatement()
    st1.execute(sql)
    st1.close()

  }

  private def createProcedure(con: Connection): Unit = {
    val sql =
      """
        |CREATE OR REPLACE PROCEDURE db_test_insert_data(a integer, b integer)
        |LANGUAGE SQL
        |AS $$
        |INSERT INTO wrapper_table_b VALUES (a,b);
        |INSERT INTO wrapper_table_b VALUES (b,a);
        |$$;
        |""".stripMargin

    val st1 = con.createStatement()
    st1.execute(sql)
    st1.close()

  }

  def createStructures(con: Connection): Unit = {
    val table1 =
      """
        |create table wrapper_table_a (
        | id SERIAL UNIQUE,
        | big_id BIGSERIAL UNIQUE,
        | bit_col BIT NOT NULL,
        | bit_varying_col BIT(5),
        | name TEXT,
        | char_col CHAR,
        | char_varying_col VARCHAR(255),
        | date_col DATE,
        | double_col DOUBLE PRECISION,
        | integer_col INTEGER,
        | numeric_col NUMERIC(9,3),
        | real_col REAL,
        | small_int_col SMALLINT,
        | text_col TEXT,
        | time_col TIME,
        | time_with_timezone_col TIME WITH TIME ZONE,
        | timestamp_col TIMESTAMP,
        | timestamp_with_timezone_col TIMESTAMP WITH TIME ZONE,
        | xml_col XML,
        | PRIMARY KEY(id, big_id) )
        |
        |""".stripMargin

    val table2 =
      """
        |create table wrapper_table_b (
        |  id SERIAL UNIQUE,
        |  table_a_id INT,
        |  PRIMARY KEY (id),
        |  FOREIGN KEY (table_a_id) REFERENCES wrapper_table_a (id)
        |)
        |""".stripMargin

    val st1 = con.createStatement()
    st1.execute(table1)
    st1.close()

    val st2 = con.createStatement()
    st2.execute(table2)
    st2.close()

    createFunction(con)

    // TODO: Get Travis-CI set up with at leat version 11 of Postgres to support procedures
//    createProcedure(con)


  }

  def dropTables(con: Connection): Unit = {
    val table2 = "drop table if exists wrapper_table_b"
    val st2 = con.createStatement()
    st2.execute(table2)
    st2.close()

    val table1 = "drop table if exists wrapper_table_a cascade"
    val st1 = con.createStatement()
    st1.execute(table1)
    st1.close()

    val sf = con.createStatement()
    sf.execute("drop function if exists db_test_add(integer, integer);")
    sf.close()

// TODO: Get Travis-CI set up with at least version 11 of Postgres to support procedures
//    val sp = con.createStatement()
//    sp.execute("drop procedure if exists db_test_insert_data(integer, integer)")
//    sp.close()
  }

}
