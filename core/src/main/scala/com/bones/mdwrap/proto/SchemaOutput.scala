package com.bones.mdwrap.proto

object SchemaOutput {

  val upperCaseF = (str: String) => str.toUpperCase
  val lowerCaseF = (str: String) => str.toLowerCase

  def postgresTableStatement(schemaName: String, protoTable: ProtoTable): String = {
    val output = PostgresSqlOutput(_.toLowerCase())
    createTableOutput(schemaName, protoTable, output.protoColumnOutput(_))
  }

  def postgresCreateColumnStatements(schemaName: String, protoColumns: List[(ProtoTable, ProtoColumn)]): String = {
    val output = PostgresSqlOutput(_.toLowerCase())
    addColumnOutput(schemaName, protoColumns, output.protoColumnOutput(_))
  }

  def createTableOutput(
    schemaName: String,
    protoTable: ProtoTable,
    columnAsCreateStatement: ProtoColumn => String) : String = {
    val types =
      protoTable.columns.map(columnAsCreateStatement).mkString(", ")
    s"""create table ${schemaName}.${protoTable.name} ($types)"""
  }

  def addColumnOutput(
    schemaName: String,
    columns: List[(ProtoTable, ProtoColumn)],
    columnAsUpdateStatement: ProtoColumn => String): String = {
    columns.map { case (table, column) => {
      s"alter table ${schemaName}.${table.name} add column ${columnAsUpdateStatement(column)};"
     }
    }.mkString("\n")
  }




}
