package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap.DatabaseQuery

abstract class DefaultLoader[A] {

  protected def loadFromQuery(databaseQuery: DatabaseQuery, con: Connection): LazyList[ResultSet]

  protected def extractRow(resultSet: ResultSet): A

  /**
   * Process a ResultSet which should be created by calling java.sql.DatabaseMetadata.getXXX methods.
   * Converts the ResultSet into a list of A objects.
   *
   * for example:
   *    loadFromResultSet(con.getMetaData.getTables(null, null, null, "myTable"))
   *
   * @param resultSet The result set to iterate through. Caller is responsible for closing this.
   * @return List of A objects
   * @throws java.sql.SQLException Propagated from calling the ResultSet methods.
   */
  def loadFromResultSet(resultSet: ResultSet): List[A] =
    new Iterator[A] {
      def hasNext: Boolean = resultSet.next()
      def next(): A = extractRow(resultSet)
    }.toList

  def load(databaseQuery: DatabaseQuery, con: Connection): List[A] = {
    val objects = for {
      resultSet <- loadFromQuery(databaseQuery, con)
      loaded <- try loadFromResultSet(resultSet) finally resultSet.close()
    } yield loaded
    objects.toList.distinct
  }

}
