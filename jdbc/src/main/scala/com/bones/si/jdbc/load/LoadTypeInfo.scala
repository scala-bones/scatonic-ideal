package com.bones.si.jdbc.load

import java.sql.{Connection, ResultSet}

import com.bones.si.jdbc.{DataType, Nullable, Searchable, TypeInfo}

object LoadTypeInfo {
  def loadAll(con: Connection): List[TypeInfo] = {
    val rs = con.getMetaData.getTypeInfo
    try loadFromResultSet(rs)
    finally rs.close
  }

  /**
    * Process a ResultSet which should be created by calling java.sql.DatabaseMetadata.getXXX methods.
    * Converts the ResultSet into a list of A objects.
    *
    * for example:
    *    loadFromResultSet(con.getMetaData.getTables(null, null, null, "myTable"))
    *
    * @param resultSet The result set to iterate through. Caller is responsible for closing this.
    * @return List of A objects
    * @throws MissingDataException Propagated from calling the ResultSet methods.
    */
  def loadFromResultSet(resultSet: ResultSet): List[TypeInfo] =
    new Iterator[TypeInfo] {
      def hasNext: Boolean = resultSet.next()
      def next(): TypeInfo = extractRow(resultSet)
    }.toList

  protected def extractRow(rs: ResultSet): TypeInfo = {
    val dataTypeInt = req(rs.getInt("DATA_TYPE"))
    val dataType = DataType
      .findByConstant(dataTypeInt)
      .getOrElse(throw new MissingDataException(s"Could not find data type by id: $dataTypeInt"))
    val nullableInt = req(rs.getInt("NULLABLE"))
    val nullable = Nullable
      .findById(nullableInt)
      .getOrElse(throw new MissingDataException(s"Could not find nullable by id: $nullableInt"))
    val searchableInt = rs.getInt("SEARCHABLE")
    val searchable = Searchable
      .findBySearchableId(searchableInt)
      .getOrElse(throw new MissingDataException(s"Could not find searchable by id: $searchableInt"))
    TypeInfo(
      req(rs.getString("TYPE_NAME")),
      dataType,
      req(rs.getInt("PRECISION")),
      Option(rs.getString("LITERAL_PREFIX")),
      Option(rs.getString("LITERAL_SUFFIX")),
      Option(rs.getString("CREATE_PARAMS")),
      nullable,
      req(rs.getBoolean("CASE_SENSITIVE")),
      searchable,
      req(rs.getBoolean("UNSIGNED_ATTRIBUTE")),
      req(rs.getBoolean("FIXED_PREC_SCALE")),
      req(rs.getBoolean("AUTO_INCREMENT")),
      Option(rs.getString("LOCAL_TYPE_NAME")),
      req(rs.getShort("MINIMUM_SCALE")),
      req(rs.getShort("MAXIMUM_SCALE")),
      req(rs.getInt("NUM_PREC_RADIX"))
    )
  }
}
