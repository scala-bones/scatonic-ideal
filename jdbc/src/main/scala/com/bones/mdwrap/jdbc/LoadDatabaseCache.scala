package com.bones.mdwrap.jdbc

import java.sql.Connection

import com.bones.mdwrap.{Column, CrossReference, DatabaseCache, DatabaseQuery, PrimaryKey, Table, TableType}
import com.bones.mdwrap.TableType.TableType

import scala.util.Try

object LoadDatabaseCache {
  def load(query: DatabaseQuery, tableTypes: List[String], borrow: Borrow[Connection]): Try[DatabaseCache] = {
    for {
      tables <- LoadTable.load(query, borrow, tableTypes)
      columns <- LoadColumn.load(query, borrow)
      primaryKeys <- LoadPrimaryKey.load(query, borrow)
      crossReferences <- LoadCrossReference.load(query, borrow)
    } yield DatabaseCache(query, tableTypes, tables, columns, crossReferences, primaryKeys)
  }
}


