package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap.{DatabaseQuery, ReverenceGeneration, Table, TableType}
import com.bones.mdwrap.jdbc.Retrieve.databaseQueryToHierarchyQuery

import scala.util.Try

object LoadTable {

  def loadAdHocTypes(databaseQuery: DatabaseQuery, con: Connection, tableType: String*) =
    load(databaseQuery, con, tableType.toList)

  def loadTypes(databaseQuery: DatabaseQuery, con: Connection, tableTypes: TableType.Val*) =
    load(databaseQuery, con, tableTypes.toList.map(_.name))

  def loadAllTypes(databaseQuery: DatabaseQuery, con: Connection) = load(databaseQuery, con, List.empty)

  def load(databaseQuery: DatabaseQuery, con: Connection, tableTypes: List[String]): List[Table] = {
    val queryParams = databaseQueryToHierarchyQuery(databaseQuery)
    val types = if (tableTypes.isEmpty) null else tableTypes.toArray
    queryParams.flatMap(param => {
      val rs = con.getMetaData.getTables(param._1.orNull, param._2.orNull, param._3.orNull, types)
      try
        loadAll(rs)
      finally
        rs.close
    }).distinct
  }

  private def loadAll(rs: ResultSet): List[Table] = {
    new Iterator[Table] {
      override def hasNext: Boolean = rs.next()
      override def next(): Table = loadTable(rs)
    }.toList
  }

  private def loadTable(rs: ResultSet): Table = {

    val tableTypeStr = rs.getString("TABLE_TYPE")
    val tableType = TableType.findByStr(tableTypeStr)
    val refGenerationStr = rs.getString("REF_GENERATION")
    val refGeneration = ReverenceGeneration.findByString(refGenerationStr)
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
