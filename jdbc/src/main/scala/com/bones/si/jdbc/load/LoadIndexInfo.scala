package com.bones.si.jdbc.load

import java.sql.{Connection, ResultSet}

import com.bones.si.jdbc.{AscDesc, IndexInfo, IndexType}

object LoadIndexInfo extends DefaultLoader[IndexInfo] {
  /**
   * Return a LazyList of ResultSet where each result set is returned by
   * the correct DatabaseMetadata.getA where A is the data being wrapped.
   *
   * @param databaseQuery Used to specify what entities are to be loaded (it could be everything).
   * @param con           The connection to use to query the DatabaseMetadata.  Do not call close() on this connection,
   *                      as that is the responsibility of the caller.
   * @return A list where the next ResultSet is not loaded until requested in order to avoid
   *         the case where processing the list stops, which would cause the remaining ResultSets
   *         to remain unclosed.
   */
  override protected def loadFromQuery(databaseQuery: DatabaseQuery, con: Connection): LazyList[ResultSet] =
    Retrieve.databaseQueryToHierarchyQuery(databaseQuery).to(LazyList).map(param =>
      con.getMetaData.getIndexInfo(param._1.orNull, param._2.orNull, param._3.orNull, false, true)
    )

  /**
   * Extracts the values of a row of IndexInfo.
   *
   * @param rs Used to extract the row
   * @return The extracted row into the wrapped dataset.
   * @throws MissingDataException  can be used when there is missing or incorrect data.
   * @throws java.sql.SQLException - This method should just let the SQLException propagated up.
   */
  override protected def extractRow(rs: ResultSet): IndexInfo = {

    val indexTypeShort = req(rs.getShort("TYPE"))
    val indexType = IndexType.findByShort(indexTypeShort).getOrElse(throw new MissingDataException(s"Could not find IndexType fro value ${indexTypeShort}"))
    val ascDesc = Option(rs.getString("ASC_OR_DESC"))
      .map(x => AscDesc.findByString(x).getOrElse(throw new MissingDataException(s"Could not find value for AscDesc: ${x}")))
    IndexInfo(
      Option(rs.getString("TABLE_CAT")),
      Option(rs.getString(("TABLE_SCHEM"))),
      req(rs.getString("TABLE_NAME")),
      req(rs.getBoolean("NON_UNIQUE")),
      Option(rs.getString("INDEX_QUALIFIER")),
      req(rs.getString("INDEX_NAME")),
      indexType,
      req(rs.getShort("ORDINAL_POSITION")),
      Option(rs.getString("COLUMN_NAME")),
      ascDesc,
      req(rs.getInt("CARDINALITY")),
      req(rs.getInt("PAGES")),
      Option(rs.getString("FILTER_CONDITION"))

    )
  }
}
