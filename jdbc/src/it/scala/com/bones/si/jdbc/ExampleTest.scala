package com.bones.si.jdbc

import com.bones.si.ideal._
import com.bones.si.jdbc.load.{DatabaseMetadataCache, DatabaseQuery, LoadDatabaseCache}
import org.postgresql.ds.PGSimpleDataSource
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class ExampleTest extends AnyFunSuite with Matchers {

  test("this is an example") {

    // First lets create a base DB ideal.

    val table1Pk =
      IdealColumn("id", IntegerType.autoIncrement, false, Some("The id of the customer"))

    val table1Columns = List(
      IdealColumn("name", StringType.unbounded, false, Some("The name of the customer")),
      IdealColumn(
        "create_date",
        TimestampType.withoutTimeZone,
        false,
        Some("when the customer was created"))
    )

    val table1 = IdealTable("customer", table1Pk, table1Columns)

    val table2Pk =
      IdealColumn("id", IntegerType.autoIncrement, false, Some("The primary id of the order."))

    val table2Columns = List(
      IdealColumn("cost", NumericType(10, 2), false, Some("The total cost of the order")),
      IdealColumn(
        "create_date",
        TimestampType.withoutTimeZone(),
        false,
        Some("The data of the order"))
    )
    val table2Fks = List(
      IdealForeignKey(
        IdealColumn(
          "customer_id",
          IntegerType(),
          false,
          Some("The id of the customer associated to the order")),
        (table1, table1Pk)
      )
    )

    val table2 = IdealTable("order", table2Pk, table2Columns, table2Fks)

    val schema = IdealSchema("public", List(table1, table2))

    val dbUrl = Option(System.getProperty("pg-url"))
      .getOrElse("jdbc:postgresql://localhost/travis?user=travis&password=secret")
    val ds = new PGSimpleDataSource()
    ds.setURL(dbUrl)
    val con = ds.getConnection

    try {

      // Only load public schema.
      val query = DatabaseQuery.everything.copy(schemaNames = List("public"))
      val initialCache = DatabaseMetadataCache.empty

      //Compare the Ideal to the cache
      val initialDiff = Diff.findDiff(initialCache, schema)

      // Create SQL String statements from the diff.
      val output = PostgresSqlOutput(_.toLowerCase)
      val sqls = output.statementsFromDiffResult(initialDiff, "public")

      // Execute the statements to sync the database with the ideal.
      sqls.foreach(str => {
//        println(str)
        val statement = con.createStatement()
        statement.execute(str)
        statement.close
      })

      // reload the cache from the database, this will pick up the above changes.
      val newCache = LoadDatabaseCache.load(query, List.empty, con)

      // Now we'll make some updates, new column, and new table.
      val newColumn = IdealColumn(
        "satisfied_rating",
        IntegerType(),
        true,
        Some("scale of 1 - 5, how satisfied was the customer with the order"))
      val newTable2 = table2.copy(columns = newColumn :: table2.columns).copy(uniqueConstraints = List(UniqueConstraint(table2.columns)))

      val table3Pk =
        IdealColumn("id", IntegerType.autoIncrement, false, Some("the id of the correspondence"))
      val table3columns = List(
        IdealColumn("title", StringType(255), true, Some("The title of the correspondence")),
        IdealColumn("body", StringType.unbounded, false, Some("the body of the correspondence"))
      )
      val table3Unique = UniqueConstraint(table3columns)
      val table3 = IdealTable("correspondence", table3Pk, table3columns, List.empty, List(table3Unique), None)

      // This is our new ideal schema
      val newSchema = IdealSchema("public", List(table1, newTable2, table3))

      // find what is missing between our new ideal and the DB cache
      val newDiff = Diff.findDiff(newCache, newSchema)

      // create sql statements to reconcile the differences.
      val updateSqls = output.statementsFromDiffResult(newDiff, "public")

//      println("UPDATE STATEMENTS")
      // sync the database with the new changes.
      updateSqls.foreach(str => {
//        println(str)
        val statement = con.createStatement()
        statement.execute(str)
        statement.close
      })

      //reload the cache and print out the public schema
      // reload the cache from the database, this will pick up the above changes.
      val finalCache = LoadDatabaseCache.load(query, List.empty, con)

//      println(finalCache.toString)

    } finally {

      //cleanup
      val cleanupSql = List(
        "drop table if exists public.order",
        "drop table if exists public.customer",
        "drop table if exists public.correspondence")
      cleanupSql.foreach(str => {
        val statement = con.createStatement()
        statement.execute(str)
        statement.close
      })

      con.close()
    }

  }

}
