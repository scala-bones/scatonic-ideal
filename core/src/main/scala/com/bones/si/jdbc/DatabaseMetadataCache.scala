package com.bones.si.jdbc.load

import com.bones.si.jdbc.{Column, CrossReference, IndexInfo, PrimaryKey, Table}

object DatabaseMetadataCache {
  def empty: DatabaseMetadataCache =
    DatabaseMetadataCache(
      DatabaseQuery.everything,
      List.empty,
      List.empty,
      List.empty,
      List.empty,
      List.empty,
      List.empty
    )
}

/**
  * Holds the metadata loaded from the DatabaseMetadata
  * @param query The query used to laod the data.
  * @param tableTypes List of table types loaded.
  * @param tables List of tables loaded.
  * @param columns List of columns loaded.
  * @param crossReferences List of CrossReference types loaded
  * @param primaryKeys List of primary keys loaded.
  */
case class DatabaseMetadataCache(
  query: DatabaseQuery,
  tableTypes: List[String],
  tables: List[Table],
  columns: List[Column],
  crossReferences: List[CrossReference],
  primaryKeys: List[PrimaryKey],
  indexInfos: List[IndexInfo]
) {

  /** Query this cache to find a table by name. */
  def findTableByName(
    schemaName: String,
    tableName: String,
    catalogName: Option[String] = None
  ): Option[Table] =
    tables.find(table =>
      table.name == tableName &&
        table.schemaName.contains(schemaName) &&
        (catalogName.isEmpty || table.catalogName == catalogName)
    )

  /** Query this cache to find a column by name within a table */
  def findColumnByName(table: Table, columnName: String): Option[Column] =
    columns.find(column =>
      column.name == columnName &&
        column.tableName == table.name &&
        column.schemaName == table.schemaName &&
        column.catalogName == table.catalogName
    )

}
