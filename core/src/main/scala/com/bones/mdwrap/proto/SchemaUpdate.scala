package com.bones.mdwrap.proto

object SchemaUpdate {

  val upperCaseF = (str: String) => str.toUpperCase
  val lowerCaseF = (str: String) => str.toLowerCase

  def toPostgresMigration(schemaName: String, protoTable: ProtoTable) = {
    val output = new PostgresSqlOutput(_.toLowerCase())
    toSqlMigration(schemaName, protoTable, output.protoColumnOutput(_))
  }

  def toSqlMigration(
    schemaName: String,
    protoTable: ProtoTable,
    columnAsCreateStatement: ProtoColumn => String) : String = {
    val types =
      protoTable.columns.map(columnAsCreateStatement).mkString(", ")
    s"""create table ${schemaName}.${protoTable.name} ($types)"""
  }

}
