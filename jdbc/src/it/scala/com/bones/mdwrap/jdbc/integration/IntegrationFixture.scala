package com.bones.mdwrap.jdbc.integration

import java.sql.Connection

import org.postgresql.ds.PGSimpleDataSource
import org.scalatest.funsuite.FixtureAnyFunSuite
import org.scalatest.{FixtureTestSuite, Outcome}

abstract class IntegrationFixture extends FixtureAnyFunSuite {

  case class FixtureParam(con: () => Connection)

  override def withFixture(test: OneArgTest): Outcome = {

    val ds = new PGSimpleDataSource() ;
    ds.setURL("jdbc:postgresql://localhost/postgres?user=travis&password=secret")
    dropTables(ds.getConnection)
    createTables(ds.getConnection)
    val theFixture = FixtureParam(() => ds.getConnection)
    try {
      withFixture(test.toNoArgTest(theFixture))
    } finally {
      dropTables(ds.getConnection)
    }

  }


  def createTables(con: Connection): Unit = {
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




    con.close()


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

    con.close()
  }

}
