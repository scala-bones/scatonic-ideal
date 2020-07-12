package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap.{DatabaseQuery, Deferrability, ImportedKeys, UpdateDeleteRule}

import scala.util.Try
import com.bones.mdwrap.jdbc.Retrieve.databaseQueryToHierarchyQuery


object LoadImportedKeys {

  def load(
            databaseQuery: DatabaseQuery,
            borrowCon: Borrow[Connection]): Try[List[ImportedKeys]] = {
    val queryParams = databaseQueryToHierarchyQuery(databaseQuery)
    borrowCon.borrow(con =>
      Try {
        queryParams
          .flatMap(param => {
            val crRs = con.getMetaData.getImportedKeys(
              param._1.orNull,
              param._2.orNull,
              param._3.orNull)
            importedKeysFromRs(crRs)

          })
          .distinct
      })
  }

  private def importedKeysFromRs(rs: ResultSet): List[ImportedKeys] = {
    val result = new Iterator[ImportedKeys] {
      def hasNext: Boolean = rs.next()
      def next(): ImportedKeys = extractRow(rs)
    }.toList
    rs.close()
    result
  }

  private def extractRow(rs: ResultSet): ImportedKeys = {
    val updateRuleId = rs.getInt("UPDATE_RULE")
    val updateRule = UpdateDeleteRule
      .findById(updateRuleId)
      .getOrElse(
        throw new IllegalStateException(s"could not find UpdateDeleteRule by id: ${updateRuleId}"))
    val deleteRuleId = rs.getInt("DELETE_RULE")
    val deleteRule = UpdateDeleteRule
      .findById(updateRuleId)
      .getOrElse(
        throw new IllegalStateException(s"could not find UpdateDeleteRule by id: ${deleteRuleId}"))
    val deferrabilityId = rs.getInt("DEFERRABILITY")
    val deferrability = Deferrability
      .findById(deferrabilityId)
      .getOrElse(
        throw new IllegalStateException(s"could not find Deferrability by id: ${deferrabilityId}"))


    ImportedKeys(
      Option(rs.getString("PKTABLE_CAT")),
      Option(rs.getString("PKTABLE_SCHEM")),
      req(rs.getString("PKTABLE_NAME")),
      req(rs.getString("PKCOLUMN_NAME")),
      Option(rs.getString("FKTABLE_CAT")),
      Option(rs.getString("FKTABLE_SCHEM")),
      req(rs.getString("FKTABLE_NAME")),
      req(rs.getString("FKCOLUMN_NAME")),
      req(rs.getShort("KEY_SEQ")),
      updateRule,
      deleteRule,
      Option(rs.getString("FK_NAME")),
      Option(rs.getString("PK_NAME")),
      deferrability
    )

  }


}
