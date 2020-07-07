package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap.{Catalog, DatabaseQuery}

import scala.util.Try

object Retrieve {


  case class Hierarchy(catalogName: Option[String], schemaName: Option[String], tableName: Option[String])

  def databaseQueryToHierarchyQuery(query: DatabaseQuery): List[(Option[String], Option[String], Option[String])] = {
    val catalogs = if (query.catalogNames.isEmpty) List(None) else query.catalogNames.map(Some(_))
    val schemas = if (query.schemaNames.isEmpty) List(None) else query.schemaNames.map(Some(_))
    val tables = if (query.tableNames.isEmpty) List(None) else query.schemaNames.map(Some(_))
    for {
      catalog <- catalogs
      schema <- schemas
      table <- tables
    } yield (catalog, schema, table)
  }

  def withConnection[X](f: Connection => X, createConnection: () => Connection): Try[X] = {
    val newConnection = createConnection()
    val doIt = Try {
      f.apply(newConnection)
    }
    newConnection.close()
    doIt
  }

  def withResultSet[X](f: ResultSet => X, createResultSet: () => ResultSet): Try[X] = {
    val resultSet = createResultSet()
    val doIt = Try {
      f(resultSet)
    }
    resultSet.close()
    doIt
  }

//  private def tableResultSet(resultSet: ResultSet): Iterable[Table] = {
//
//  }

  def getAllTables(connection: () => Connection) = {

  }



  def getCatalogs(databaseQuery: DatabaseQuery)(borrowCon: Borrow[Connection]): Try[List[Catalog]] = {
    borrowCon.borrow(con => {
      catalogFromResultSet(con.getMetaData.getCatalogs).filter(databaseQuery.catalogNames.contains(_))
    })
  }

  private def catalogFromResultSet(rs: ResultSet): Try[List[Catalog]] = {
    if (rs.next()) {
      for {
        catalog <- Try { Option(rs.getString(Catalog.catalogColumnName)).toList.map(Catalog(_, List.empty)) }
        catalogs <- catalogFromResultSet(rs)
      } yield catalog ::: catalogs
    } else {
      Try { List.empty }
    }
  }

//  def getSchemas(catalogs: List[Catalog], databaseQuery: DatabaseQuery)(borrowCon: Borrow[Connection]): Try[List[Catalog]] = {
//    catalogs.map(catalog => {
//      borrowCon.borrow(con => {
//        con.getMetaData.getSchemas(catalog.name, databaseQuery.)
//      })
//    })
//  }




  def createWrappedStructure(databaseQuery: DatabaseQuery)(borrowCon: Borrow[Connection]): List[Catalog] = ???

}
