package com.bones.mdwrap

object DatabaseQuery {
  def everything: DatabaseQuery =
    DatabaseQuery(List.empty, List.empty, List.empty)
}

case class DatabaseQuery(catalogNames: List[String],
                         schemaNames: List[String],
                         tableNames: List[String]) {

  /** Reduces the search to only the specified catalog name(s).
    * @param catalogName Must be an exact match to how the schema name is stored in the database
    * @return A new DatabaseQuery copy with the specified catalogName(s) prefixed to the existing catalog names.
    */
  def catalogs(catalogName: String*): DatabaseQuery =
    copy(catalogNames = catalogName.toList ::: catalogNames)

  /**
    * Reduces the search to only the specific schema name(s).
    * @param schemaName Must be an exact match to how the schema name is stored in the database
    * @return A new DatabaseQuery copy with the specified schemaName(s) prefixed to the existing schema names.
    */
  def schemas(schemaName: String*): DatabaseQuery =
    copy(schemaNames = schemaName.toList ::: schemaNames)

  /**
    * Reduces the search to only the specific table name(s).
    * @param tableName Must be an exact match to how the shema name is stored in the database
    * @return A new DatabaseQuery copy with the specified tableName(s) prefixed to the existing table names.
    */
  def tables(tableName: String*): DatabaseQuery =
    copy(tableNames = tableName.toList ::: tableNames)

}
