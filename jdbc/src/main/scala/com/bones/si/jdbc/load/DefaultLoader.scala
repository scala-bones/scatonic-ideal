package com.bones.si.jdbc.load

import java.sql.{Connection, ResultSet}

/**
 * Provides basic methods to extract DatabaseMetadata entiies.
 * @tparam A The type of Entity being loaded, eg Table, PrimaryKey, etc.
 */
abstract class DefaultLoader[A] {

  /**
   * Return a Stream of ResultSet where each result set is returned by
   * the correct DatabaseMetadata.getA where A is the data being wrapped.
   * @param databaseQuery Used to specify what entities are to be loaded (it could be everything).
   * @param con The connection to use to query the DatabaseMetadata.  Do not call close() on this connection,
   *            as that is the responsibility of the caller.
   * @return A list where the next ResultSet is not loaded until requested in order to avoid
   *         the case where processing the list stops, which would cause the remaining ResultSets
   *         to remain unclosed.
   */
  protected def loadFromQuery(databaseQuery: DatabaseQuery, con: Connection): Stream[ResultSet]

  /**
   * Extend this method to extract a single row from the result set.  This
   * method should not call next() or close() on the resultSEt.
   * @param resultSet Used to extract the row
   * @return The extracted row into the wrapped dataset.
   * @throws MissingDataException can be used when there is missing or incorrect data.
   * @throws java.sql.SQLException - This method should just let the SQLException propagated up.
   */
  protected def extractRow(resultSet: ResultSet): A

  /**
   * Process a ResultSet which should be created by calling java.sql.DatabaseMetadata.getXXX methods.
   * Converts the ResultSet into a list of A objects.
   *
   * for example:
   *    LoadTable.loadFromResultSet(con.getMetaData.getTables(null, null, null, "myTable"))
   *
   * @param resultSet The result set to iterate through. Caller is responsible for closing this.
   * @return List of A objects
   * @throws MissingDataException Propagated from calling the ResultSet methods.
   */
  def loadFromResultSet(resultSet: ResultSet): List[A] =
    new Iterator[A] {
      def hasNext: Boolean = resultSet.next()
      def next(): A = extractRow(resultSet)
    }.toList

  /**
   * Load data given the query to connection.
   * @param databaseQuery
   * @param con
   * @return
   * @throws MissingDataException
   */
  def load(databaseQuery: DatabaseQuery, con: Connection): List[A] = {
    val objects = for {
      resultSet <- loadFromQuery(databaseQuery, con)
      loaded <- try loadFromResultSet(resultSet) finally resultSet.close()
    } yield loaded
    objects.toList.distinct
  }

}
