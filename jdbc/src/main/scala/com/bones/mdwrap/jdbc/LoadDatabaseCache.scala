package com.bones.mdwrap.jdbc

import java.sql.Connection

import com.bones.mdwrap.{DatabaseCache, DatabaseQuery}

object LoadDatabaseCache {
  def load(query: DatabaseQuery, tableTypes: List[String], con: Connection): DatabaseCache = {
    val tables = LoadTable.load(query, con, tableTypes)
    val columns = LoadColumn.load(query, con)
    val primaryKeys = LoadPrimaryKey.load(query, con)
    val crossReferences = LoadCrossReference.load(query, con)
    DatabaseCache(query, tableTypes, tables, columns, crossReferences, primaryKeys)
  }
}
