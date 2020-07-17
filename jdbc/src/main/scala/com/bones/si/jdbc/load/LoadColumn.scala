package com.bones.si.jdbc.load

import java.sql.{Connection, ResultSet}

import com.bones.si.jdbc.{Column, DataType, Nullable, YesNo}

object LoadColumn extends DefaultLoader[Column] {


  override protected def loadFromQuery(databaseQuery: DatabaseQuery, con: Connection): LazyList[ResultSet] = {
    val queryParams = Retrieve.databaseQueryToHierarchyQuery(databaseQuery)
    queryParams.to(LazyList).map(queryParam =>
      con.getMetaData
        .getColumns(queryParam._1.orNull, queryParam._2.orNull, queryParam._3.orNull, null)
    )
  }


  override protected def extractRow(rs: ResultSet): Column = {
    val dataTypeId = rs.getInt(Column.dataTypeCol)
    val dataType = DataType
      .findByConstant(dataTypeId)
      .getOrElse(throw new MissingDataException(s"Unknown DataType typeId: ${dataTypeId}"))
    val isNullableStr = rs.getString(Column.isNullableCol)
    val isNullable = YesNo
      .findByString(isNullableStr)
      .getOrElse(throw new MissingDataException(s"Unknown nullable str: ${isNullableStr}"))
    val isAutoIncrementStr = rs.getString(Column.isAutoIncrementCol)
    val isAutoIncrement = YesNo
      .findByString(isAutoIncrementStr)
      .getOrElse(
        throw new MissingDataException(s"Unknown isAutoIncrement str: ${isAutoIncrementStr}"))
    val isGeneratedColumnStr = rs.getString(Column.isGeneratedColumnCol)
    val isGeneratedColumn = YesNo.findByString(isGeneratedColumnStr) getOrElse (throw new MissingDataException(
      s"Unknown isGeneratedColumnStr str: ${isGeneratedColumnStr}"))
    val nullableStr = rs.getInt(Column.nullableCol)
    val nullable = Nullable
      .findById(nullableStr)
      .getOrElse(throw new MissingDataException(s"Unknown nullable str: ${nullableStr}"))
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
