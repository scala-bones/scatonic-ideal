package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap.jdbc.Retrieve.databaseQueryToHierarchyQuery
import com.bones.mdwrap.{DatabaseQuery, Function, FunctionType}

import scala.util.Try

object LoadFunction {

  def load(databaseQuery: DatabaseQuery, con: Connection): Try[List[Function]] = {
    val queryParams = databaseQueryToHierarchyQuery(databaseQuery)
    Try {
      queryParams
        .flatMap(param => {
          val crRs = con.getMetaData.getFunctions(param._1.orNull, param._2.orNull, param._3.orNull)
          try functionsFromRs(crRs)
          finally crRs.close()
        })
        .distinct
    }
  }

  def functionsFromRs(rs: ResultSet): List[Function] = {
    new Iterator[Function] {
      def hasNext: Boolean = rs.next()
      def next(): Function = extractRow(rs)
    }.toList
  }

  def extractRow(rs: ResultSet): Function = {
    val functionTypeId = req(rs.getInt("FUNCTION_TYPE"))
    val functionType = FunctionType
      .findById(functionTypeId)
      .getOrElse(
        throw new IllegalStateException(s"could not find function type: ${functionTypeId}"))
    Function(
      Option(rs.getString("FUNCTION_CAT")),
      Option(rs.getString("FUNCTION_SCHEM")),
      req(rs.getString("FUNCTION_NAME")),
      Option(rs.getString("REMARKS")),
      functionType,
      req(rs.getString("SPECIFIC_NAME"))
    )
  }

}
