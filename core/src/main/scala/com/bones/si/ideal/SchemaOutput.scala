package com.bones.si.ideal

import com.bones.si.ideal.Diff._

object SchemaOutput {
  val postgresLowercase = new SchemaOutput(_.toLowerCase, DataTypePostgresSqlOutput(_.toLowerCase))
  val postgresUppercase = new SchemaOutput(_.toUpperCase, DataTypePostgresSqlOutput(_.toUpperCase))
}

/**
  * Responsible for producing Output from a Diff.  In other words, what SQL statements need to
  * be executed in order for the DB to be in sync with the ideal.
  */
class SchemaOutput(toCase: String => String, dataTypeOutput: DataTypeOutput[String]) {

  private val PRIMARY_KEY = toCase("primary key")
  private val FOREIGN_KEY = toCase("foreign key")
  private val REFERENCES = toCase("references")
  private val COMMENT_ON_TABLE = toCase("comment on table")
  private val COMMENT_ON_COLUMN = toCase("comment on column")
  private val CREATE_TABLE = toCase("create table")
  private val ALTER_TABLE = toCase("alter table")
  private val ADD_COLUMN = toCase("add column")
  private val IS = toCase("is")
  private val ALTER_COLUMN = toCase("alter column")
  private val NOT_NULL = toCase("not null")
  private val DROP = toCase("drop")
  private val SET = toCase("set")

  /**
    * Produces sql statements in lower case to create/update tables, columns, primary keys, and foreign keys.
    * @param diffResult The statements created are based off of this diff.
    * @param schemaName What schema to create the DB Structures.
    * @return
    */
  def statementsFromDiffResult(diffResult: DiffResult, schemaName: String): List[String] = {
    val tableSqls = diffResult.tablesMissing.flatMap(createTableOutput(schemaName, _))
    val columnSqls =
      addColumnOutput(schemaName, diffResult.columnsMissing)
    val updateColumnsSqls =
      diffResult.columnsDifferent.flatMap(c =>
        alterColumnOutput(schemaName, c._1.name, (c._2, c._3)))

    tableSqls ::: columnSqls ::: updateColumnsSqls
  }

  /** Create SQL statement to create the table from the IdealTable definition
    * @param schemaName The name of the schema used in the create table statement
    * @param idealTable What the "ideal" table should look like.
    */
  def createTableOutput(schemaName: String, idealTable: IdealTable): List[String] = {

    val primaryKeys = idealTable.primaryKeyColumns.map(pk => dataTypeOutput.protoColumnOutput(pk))
    val (pkColumnCreate, pkTrailingDef) =
      if (primaryKeys.isEmpty)
        (List.empty, List.empty)
      else if (primaryKeys.length == 1)
        (primaryKeys.map(_ + " " + PRIMARY_KEY), List.empty)
      else {
        val keyNames = idealTable.primaryKeyColumns.map(_.name).mkString("(", ", ", ")")
        (primaryKeys, List(s"$PRIMARY_KEY $keyNames"))
      }

    val fkColumn = idealTable.foreignKeys.map(fk => {
      val fkColumn = dataTypeOutput.protoColumnOutput(fk.column)
      val fkTrailingDef =
        (s"$FOREIGN_KEY (${fk.column.name}) $REFERENCES ${fk.foreignReference._1.name} (${fk.foreignReference._2.name})")
      (fkColumn, fkTrailingDef)
    })

    val allColumns =
      pkColumnCreate ::: idealTable.columns.map(dataTypeOutput.protoColumnOutput) ::: fkColumn.map(
        _._1) ::: pkTrailingDef ::: fkColumn
        .map(_._2) ::: Nil

    val remark = idealTable.remark
      .map(remark => s"$COMMENT_ON_TABLE $schemaName.${idealTable.name} $IS '$remark'")
      .toList

    val columnRemarks = idealTable.allColumns.flatMap(c => c.remark.map(rem => (c.name, rem))).map {
      case (cName, rem) => s"$COMMENT_ON_COLUMN $schemaName.${idealTable.name}.${cName} $IS '$rem'"
    }

    val tableSql =
      s"""$CREATE_TABLE $schemaName.${idealTable.name} (${allColumns.mkString(", ")})"""

    tableSql :: remark ::: columnRemarks

  }

  def addColumnOutput(
    schemaName: String,
    columns: List[(IdealTable, IdealColumn)]): List[String] = {
    val columnSql = columns.map {
      case (table, column) =>
        s"$ALTER_TABLE $schemaName.${table.name} $ADD_COLUMN ${dataTypeOutput.protoColumnOutput(column)}"
    }
    val commentSql = columns
      .flatMap(x => x._2.remark.map((x, _)))
      .map(rem => {
        s"$COMMENT_ON_COLUMN $schemaName.${rem._1._1.name}.${rem._1._2.name} $IS '${rem._2}'"
      })
    columnSql ::: commentSql
  }

  def alterColumnOutput(
    schemaName: String,
    tableName: String,
    columnDiff: (IdealColumn, List[ColumnDiff])): List[String] = {
    columnDiff._2.map {
      case ColumnDataTypeDiff(_, _) =>
        s"$ALTER_TABLE $schemaName.$tableName $ALTER_COLUMN ${columnDiff._1.name} ${dataTypeOutput
          .dataTypeOutput(columnDiff._1.dataType)}"
      case ColumnRemarkDiff(_, _) =>
        s"$COMMENT_ON_COLUMN $schemaName.$tableName.${columnDiff._1.name} $IS '${columnDiff._1.remark
          .getOrElse("")}'"
      case ColumnNullableDiff(_, _) =>
        val setDrop = if (columnDiff._1.nullable) DROP else SET
        s"$ALTER_TABLE $schemaName.$tableName $ALTER_COLUMN $setDrop $NOT_NULL"
    }
  }

}
