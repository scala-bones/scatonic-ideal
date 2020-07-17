package com.bones.si.ideal

import com.bones.si.ideal.Diff._

object SchemaOutput {

  def fromDiffResult(diffResult: DiffResult, schemaName: String): List[String] = {
    val output = PostgresSqlOutput(_.toLowerCase())

    val tableSqls = diffResult.tablesMissing.flatMap(SchemaOutput.postgresTableStatement(schemaName, _))
    val columnSqls =
      SchemaOutput.postgresCreateColumnStatements(schemaName, diffResult.columnsMissing)
    val updateColumnsSqls =
      diffResult.columnsDifferent.flatMap(c => SchemaOutput.alterColumnOutput(schemaName,c._1.name, (c._2, c._3), output.dataTypeOutput))

     tableSqls ::: columnSqls ::: updateColumnsSqls

  }

  def postgresTableStatement(schemaName: String, protoTable: IdealTable): List[String] = {
    val output = PostgresSqlOutput(_.toLowerCase())
    createTableOutput(schemaName, protoTable, output.protoColumnOutput, _.toLowerCase())
  }

  def postgresCreateColumnStatements(
    schemaName: String,
    protoColumns: List[(IdealTable, IdealColumn)]): List[String] = {
    val output = PostgresSqlOutput(_.toLowerCase())
    addColumnOutput(schemaName, protoColumns, output.protoColumnOutput)
  }

  def createTableOutput(
    schemaName: String,
    protoTable: IdealTable,
    columnAsCreateStatement: IdealColumn => String,
    toCase: String => String): List[String] = {

    val primaryKeys = protoTable.primaryKeyColumns.map(pk => columnAsCreateStatement(pk))
    val (pkColumnCreate, pkTrailingDef) =
      if (primaryKeys.isEmpty)
        (List.empty, List.empty)
      else if (primaryKeys.length == 1)
        (primaryKeys.map(_ + toCase(" primary key")), List.empty)
      else {
        val keyNames = protoTable.primaryKeyColumns.map(_.name).mkString("(", ", ", ")")
        (primaryKeys, List(toCase(s"primary key $keyNames")))
      }

    val fkColumn = protoTable.foreignKeys.map(fk => {
      val fkColumn = columnAsCreateStatement(fk.column)
      val FOREIGN_KEY = toCase("foreign key")
      val REFERENCES = toCase("references")
      val fkTrailingDef =
        (s"$FOREIGN_KEY (${fk.column.name}) $REFERENCES ${fk.foreignReference._1.name} (${fk.foreignReference._2.name})")
      (fkColumn, fkTrailingDef)
    })

    val allColumns =
      pkColumnCreate ::: protoTable.columns.map(columnAsCreateStatement) ::: fkColumn.map(_._1) ::: pkTrailingDef ::: fkColumn
        .map(_._2) ::: Nil

    val remark = protoTable.remark
      .map(remark => s"comment on table $schemaName.${protoTable.name} is '$remark'")
      .toList

    val columnRemarks = protoTable.allColumns.flatMap(c => c.remark.map(rem => (c.name, rem))).map {
      case (cName, rem) => s"comment on column $schemaName.${protoTable.name}.${cName} is '$rem'"
    }

    val tableSql = s"""create table $schemaName.${protoTable.name} (${allColumns.mkString(", ")})"""

    tableSql :: remark ::: columnRemarks

  }

  def addColumnOutput(
    schemaName: String,
    columns: List[(IdealTable, IdealColumn)],
    columnAsUpdateStatement: IdealColumn => String): List[String] = {
    val columnSql = columns.map {
      case (table, column) =>
        s"alter table $schemaName.${table.name} add column ${columnAsUpdateStatement(column)}"
    }
    val commentSql = columns.flatMap(x => x._2.remark.map( (x, _))).map(rem => {
      s"comment on column $schemaName.${rem._1._1.name}.${rem._1._2.name} is '${rem._2}'"
    })
    columnSql ::: commentSql
  }

  def alterColumnOutput(
    schemaName: String,
    tableName: String,
    columnDiff: (IdealColumn, List[ColumnDiff]),
    f: IdealDataType => String): List[String] = {
    columnDiff._2.map {
      case ColumnDataTypeDiff(_, _) =>
        s"alter table $schemaName.$tableName alter column ${columnDiff._1.name} ${f(columnDiff._1.dataType)}"
      case ColumnRemarkDiff(_, _) =>
        s"comment on column $schemaName.$tableName.${columnDiff._1.name} is '${columnDiff._1.remark.getOrElse("")}'"
      case ColumnNullableDiff(_, _) =>
        val setDrop = if (columnDiff._1.nullable) "drop" else "set"
        s"alter table $schemaName.$tableName alter column $setDrop not null"
    }
  }

}
