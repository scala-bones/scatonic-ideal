package com.bones.si.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.si.jdbc.Retrieve.databaseQueryToHierarchyQuery
import com.bones.si.{DatabaseQuery, Function, FunctionType}

object LoadFunction extends DefaultLoader[Function] {


  override protected def loadFromQuery(databaseQuery: DatabaseQuery, con: Connection): LazyList[ResultSet] =
    databaseQueryToHierarchyQuery(databaseQuery).to(LazyList).map(param => {
      con.getMetaData.getFunctions(param._1.orNull, param._2.orNull, param._3.orNull)
    })

  override protected def extractRow(rs: ResultSet): Function = {
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
