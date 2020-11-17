package com.bones.si.jdbc.load

import java.sql.{Connection, ResultSet}

import com.bones.si.jdbc.{ReferenceGeneration, Table, TableType}

object LoadTable extends DefaultLoader[Table] {

  def loadAdHocTypes(databaseQuery: DatabaseQuery, con: Connection, tableType: String*) =
    loadCustomTypes(databaseQuery, con, tableType.toList)

  def loadTypes(databaseQuery: DatabaseQuery, con: Connection, tableTypes: TableType.Val*) =
    loadCustomTypes(databaseQuery, con, tableTypes.toList.map(_.name))

  def loadAllTypes(databaseQuery: DatabaseQuery, con: Connection) = loadCustomTypes(databaseQuery, con, List.empty)


  override protected def loadFromQuery(databaseQuery: DatabaseQuery, con: Connection): Stream[ResultSet] =
    Retrieve.databaseQueryToHierarchyQuery(databaseQuery).toStream.map(param =>
      con.getMetaData.getTables(param._1.orNull, param._2.orNull, param._3.orNull, null)
    )

  def loadCustomTypes(databaseQuery: DatabaseQuery, con: Connection, tableTypes: List[String]): List[Table] = {
    val queryParams = Retrieve.databaseQueryToHierarchyQuery(databaseQuery)
    val types = if (tableTypes.isEmpty) null else tableTypes.toArray
    queryParams.flatMap(param => {
      val rs = con.getMetaData.getTables(param._1.orNull, param._2.orNull, param._3.orNull, types)
      try
        loadFromResultSet(rs)
      finally
        rs.close
    }).distinct
  }

  protected def extractRow(rs: ResultSet): Table = {

    val tableTypeStr = rs.getString("TABLE_TYPE")
    val tableType = TableType.findByStr(tableTypeStr)
    val refGenerationStr = rs.getString("REF_GENERATION")
    val refGeneration = ReferenceGeneration.findByString(refGenerationStr)
    Table(
      Option(rs.getString("TABLE_CAT")),
      Option(rs.getString(("TABLE_SCHEM"))),
      req(rs.getString("TABLE_NAME")),
      tableType,
      Option(rs.getString("REMARKS")),
      strOption(rs.getString("TYPE_CAT")),
      strOption(rs.getString("TYPE_SCHEM")),
      strOption(rs.getString("TYPE_NAME")),
      strOption(rs.getString("SELF_REFERENCING_COL_NAME")),
      refGeneration
    )

  }

}
