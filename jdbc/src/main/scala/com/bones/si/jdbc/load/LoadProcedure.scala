package com.bones.si.jdbc.load

import java.sql.{Connection, ResultSet}

import com.bones.si.jdbc.{Procedure, ProcedureType}

object LoadProcedure extends DefaultLoader[Procedure] {
  override protected def loadFromQuery(
    databaseQuery: DatabaseQuery,
    con: Connection
  ): LazyList[ResultSet] =
    Retrieve
      .databaseQueryToProcedureQuery(databaseQuery)
      .to(LazyList)
      .map(param =>
        con.getMetaData.getProcedures(param._1.orNull, param._2.orNull, param._3.orNull)
      )

  override protected def extractRow(rs: ResultSet): Procedure = {
    val procedureTypeInt = req(rs.getInt("PROCEDURE_TYPE"))
    val procedureType = ProcedureType
      .findByProcedureTypeId(procedureTypeInt)
      .getOrElse(
        throw new MissingDataException(s"could not find procedure with id: $procedureTypeInt")
      )
    Procedure(
      Option(rs.getString("PROCEDURE_CAT")),
      Option(rs.getString("PROCEDURE_SCHEM")),
      req(rs.getString("PROCEDURE_NAME")),
      Option(rs.getString("REMARKS")),
      procedureType,
      req(rs.getString("SPECIFIC_NAME"))
    )
  }
}
