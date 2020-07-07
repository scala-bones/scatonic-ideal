package com.bones.mdwrap

case class DatabaseCache(query: DatabaseQuery,
                         tableTypes: List[String],
                         tables: List[Table],
                         columns: List[Column],
                         crossReferences: List[CrossReference],
                         primaryKeys: List[PrimaryKey]) {

  def findTableByName(schemaName: String,
                      tableName: String,
                      catalogName: Option[String] = None): Option[Table] =
    tables.find(
      table =>
        table.name == tableName &&
          table.schemaName.contains(schemaName) &&
          (catalogName.isEmpty || table.catalogName == catalogName))

  def findColumnByName(table: Table, columnName: String): Option[Column] =
    columns.find(
      column =>
        column.name == columnName &&
          column.tableName == table.name &&
          column.schemaName == table.schemaName &&
          column.catalogName == table.catalogName)

}
