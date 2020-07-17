package com.bones.si.jdbc.load

import java.sql.Connection

import com.bones.si.jdbc

object LoadDatabaseCache {
  def load(query: DatabaseQuery, tableTypes: List[String], con: Connection): DatabaseMetadataCache = {
    val tables = LoadTable.loadCustomTypes(query, con, tableTypes)
    val columns = LoadColumn.load(query, con)
    val primaryKeys = LoadPrimaryKey.load(query, con)
    val crossReferences = LoadCrossReference.load(query, con)
    DatabaseMetadataCache(query, tableTypes, tables, columns, crossReferences, primaryKeys)
  }
}
