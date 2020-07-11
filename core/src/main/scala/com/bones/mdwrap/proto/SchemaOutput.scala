package com.bones.mdwrap.proto

object SchemaOutput {

  def postgresTableStatement(schemaName: String, protoTable: ProtoTable): String = {
    val output = PostgresSqlOutput(_.toLowerCase())
    createTableOutput(schemaName, protoTable, output.protoColumnOutput, _.toLowerCase())
  }

  def postgresCreateColumnStatements(
    schemaName: String,
    protoColumns: List[(ProtoTable, ProtoColumn)]): String = {
    val output = PostgresSqlOutput(_.toLowerCase())
    addColumnOutput(schemaName, protoColumns, output.protoColumnOutput)
  }

  def createTableOutput(
    schemaName: String,
    protoTable: ProtoTable,
    columnAsCreateStatement: ProtoColumn => String,
    toCase: String => String): String = {

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
    val allColumns =
      pkColumnCreate ::: protoTable.columns.map(columnAsCreateStatement) ::: pkTrailingDef ::: Nil

    //TODO: foreign keys
    s"""create table $schemaName.${protoTable.name} (${allColumns.mkString(", ")})"""
  }

  def addColumnOutput(
    schemaName: String,
    columns: List[(ProtoTable, ProtoColumn)],
    columnAsUpdateStatement: ProtoColumn => String): String = {
    columns
      .map {
        case (table, column) =>
          s"alter table $schemaName.${table.name} add column ${columnAsUpdateStatement(column)};"
      }
      .mkString("\n")
  }

}
