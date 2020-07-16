package com.bones.si.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.si.{Catalog, DatabaseQuery}

object Retrieve {

  case class Hierarchy(
    catalogName: Option[String],
    schemaName: Option[String],
    tableName: Option[String])

  private def catalogQuery(query: DatabaseQuery) =
    if (query.catalogNames.isEmpty) List(None) else query.catalogNames.map(Some(_))

  private def schemaQuery(query: DatabaseQuery) =
    if (query.schemaNames.isEmpty) List(None) else query.schemaNames.map(Some(_))
  def databaseQueryToHierarchyQuery(
    query: DatabaseQuery): List[(Option[String], Option[String], Option[String])] = {
    val catalogs = catalogQuery(query)
    val schemas = schemaQuery(query)
    val tables = if (query.tableNames.isEmpty) List(None) else query.tableNames.map(Some(_))
    for {
      catalog <- catalogs
      schema <- schemas
      table <- tables
    } yield (catalog, schema, table)
  }

  def databaseQueryToProcedureQuery(
                                     query: DatabaseQuery): List[(Option[String], Option[String], Option[String])] = {
    val catalogs = catalogQuery(query)
    val schemas = schemaQuery(query)
    val procedures =
      if (query.procedureNames.isEmpty) List(None) else query.procedureNames.map(Some(_))

    for {
      catalog <- catalogs
      schema <- schemas
      procedure <- procedures
    } yield (catalog, schema, procedure)
  }

  def databaseQueryToFunctionQuery(
    query: DatabaseQuery): List[(Option[String], Option[String], Option[String])] = {
    val catalogs = catalogQuery(query)
    val schemas = schemaQuery(query)
    val functions =
      if (query.functionNames.isEmpty) List(None) else query.functionNames.map(Some(_))

    for {
      catalog <- catalogs
      schema <- schemas
      function <- functions
    } yield (catalog, schema, function)
  }

  def databaseQueryToAttributeQuery(
    query: DatabaseQuery): List[(Option[String], Option[String], Option[String])] = {
    val catalogs = catalogQuery(query)
    val schemas = schemaQuery(query)
    val attributes =
      if (query.attributeNames.isEmpty) List(None) else query.attributeNames.map(Some(_))
    for {
      catalog <- catalogs
      schema <- schemas
      attribute <- attributes
    } yield (catalog, schema, attribute)
  }

  def getCatalogs(databaseQuery: DatabaseQuery, con: Connection): List[Catalog] =
    catalogFromResultSet(con.getMetaData.getCatalogs).filter(databaseQuery.catalogNames.contains(_))

  private def catalogFromResultSet(rs: ResultSet): List[Catalog] =
    new Iterator[Catalog] {
      override def hasNext: Boolean = rs.next()
      override def next(): Catalog = Catalog(rs.getString(Catalog.catalogColumnName))
    }.toList

}
