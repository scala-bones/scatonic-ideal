package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap._
import com.bones.mdwrap.jdbc.Retrieve.databaseQueryToHierarchyQuery

import scala.util.Try

object LoadColumn {

  def load(databaseQuery: DatabaseQuery, con: Connection): List[Column] = {
    val queryParams = databaseQueryToHierarchyQuery(databaseQuery)
    queryParams
      .flatMap(queryParam => {
        val resultSet = con.getMetaData
          .getColumns(queryParam._1.orNull, queryParam._2.orNull, queryParam._3.orNull, null)
        try columnsFromResultSet(resultSet)
        finally resultSet.close()
      })
      .distinct
  }

  def columnsFromResultSet(rs: ResultSet): List[Column] = {
    val result = new Iterator[Column] {
      def hasNext = rs.next()
      def next() = extractRow(rs)
    }.toList
    result
  }

  private def extractRow(rs: ResultSet): Column = {
    val dataTypeId = rs.getInt(Column.dataTypeCol)
    val dataType = DataType
      .findByConstant(dataTypeId)
      .getOrElse(throw new IllegalStateException(s"Unknown DataType typeId: ${dataTypeId}"))
    val isNullableStr = rs.getString(Column.isNullableCol)
    val isNullable = YesNo
      .findByString(isNullableStr)
      .getOrElse(throw new IllegalStateException(s"Unknown nullable str: ${isNullableStr}"))
    val isAutoIncrementStr = rs.getString(Column.isAutoIncrementCol)
    val isAutoIncrement = YesNo
      .findByString(isAutoIncrementStr)
      .getOrElse(
        throw new IllegalStateException(s"Unknown isAutoIncrement str: ${isAutoIncrementStr}"))
    val isGeneratedColumnStr = rs.getString(Column.isGeneratedColumnCol)
    val isGeneratedColumn = YesNo.findByString(isGeneratedColumnStr) getOrElse (throw new IllegalStateException(
      s"Unknown isGeneratedColumnStr str: ${isGeneratedColumnStr}"))
    val nullableStr = rs.getInt(Column.nullableCol)
    val nullable = Nullable
      .findById(nullableStr)
      .getOrElse(throw new IllegalStateException(s"Unknown nullable str: ${nullableStr}"))
    Column(
      Option(rs.getString(Column.categoryNameCol)),
      Option(rs.getString(Column.schemaNameCol)),
      req(rs.getString(Column.tableNameCol)),
      req(rs.getString(Column.nameCol)),
      dataType,
      req(rs.getString(Column.typeNameCol)),
      req(rs.getInt(Column.columnSizeCol)),
      Option(rs.getInt(Column.decimalDigitsCol)),
      req(rs.getInt(Column.numProcRadixCol)),
      nullable,
      Option(rs.getString(Column.remarksCol)),
      Option(rs.getString(Column.columnDefaultCol)),
      req(rs.getInt(Column.characterOctetLengthCol)),
      req(rs.getInt(Column.ordinalPositionCol)),
      isNullable,
      Option(rs.getShort(Column.sourceDataTypeCol)),
      isAutoIncrement,
      isGeneratedColumn
    )
  }

}
