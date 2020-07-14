package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap.jdbc.Retrieve.databaseQueryToHierarchyQuery
import com.bones.mdwrap.{DatabaseQuery, PrimaryKey}

object LoadPrimaryKey {

  def load(query: DatabaseQuery, con: Connection): List[PrimaryKey] = {
    databaseQueryToHierarchyQuery(query)
      .flatMap(q => {
        val rs = con.getMetaData.getPrimaryKeys(q._1.orNull, q._2.orNull, q._3.orNull)
        try extractPrimaryKeys(rs) finally rs.close
      })
  }

  private def extractPrimaryKeys(rs: ResultSet): List[PrimaryKey] = {
    new Iterator[PrimaryKey] {
      override def hasNext: Boolean = rs.next()
      override def next(): PrimaryKey = loadRow(rs)
    }.toList
  }

  private def loadRow(rs: ResultSet): PrimaryKey = {
    PrimaryKey(
      Option(rs.getString("TABLE_CAT")),
      Option(rs.getString("TABLE_SCHEM")),
      req(rs.getString("TABLE_NAME")),
      req(rs.getString("COLUMN_NAME")),
      req(rs.getShort("KEY_SEQ")),
      Option(rs.getString("PK_NAME"))
    )
  }

}
