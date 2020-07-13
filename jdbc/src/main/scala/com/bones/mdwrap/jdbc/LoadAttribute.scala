package com.bones.mdwrap.jdbc

import java.sql.{Connection, ResultSet}

import com.bones.mdwrap.{DbAttribute, DataType, DatabaseQuery, Nullable, YesNo}
import com.bones.mdwrap.jdbc.Retrieve.databaseQueryToAttributeQuery

import scala.util.Try

object LoadAttribute {


  def load(databaseQuery: DatabaseQuery, borrow: Borrow[Connection]): Try[List[DbAttribute]] = {
    val queryParams = databaseQueryToAttributeQuery(databaseQuery)
    borrow.borrow(con =>
      Try {
        queryParams
          .flatMap(queryParam => {
            val resultSet = con.getMetaData
              .getAttributes(queryParam._1.orNull, queryParam._2.orNull, null, queryParam._3.orNull)
            attributesFromResultSet(resultSet)
          })
          .distinct
      })
  }

  private def attributesFromResultSet(rs: ResultSet): List[DbAttribute] = {
    val result = new Iterator[DbAttribute] {
      def hasNext = rs.next()
      def next() = extractRow(rs)
    }.toList
    rs.close()
    result
  }

  private def extractRow(rs: ResultSet) : DbAttribute = {
    val dataTypeInt = req(rs.getInt("DATA_TYPE"))
    val dataType = DataType.findByConstant(dataTypeInt).getOrElse(throw new IllegalStateException(s"could not find data type with id $dataTypeInt"))
    val nullableInt = req(rs.getInt("NULLABLE"))
    val nullable = Nullable.findById(nullableInt).getOrElse(throw new IllegalStateException(s"could  not find nullable with id ${nullableInt}"))
    val isNullableStr = req(rs.getString("IS_NULLABLE"))
    val isNullable = YesNo.findByString(isNullableStr).getOrElse(throw new IllegalStateException(s"could not fin isNullable with str: $isNullableStr"))
    DbAttribute(
      Option(rs.getString("TYPE_CAT")),
      Option(rs.getString("TYPE_SCHEM")),
      req(rs.getString("TYPE_NAME")),
      req(rs.getString("ATTR_NAME")),
      dataType,
      req(rs.getString("ATTR_TYPE_NAME")),
      req(rs.getInt("ATTR_SIZE")),
      Option(rs.getInt("DECIMAL_DIGITS")),
      req(rs.getInt("NUM_PREC_RADIX")),
      nullable,
      Option(rs.getString("REMARKS")),
      Option(rs.getString("ATTR_DEF")),
      Option(rs.getInt("CHAR_OCTET_LENGTH")),
      req(rs.getInt("ORDINAL_POSITION")),
      isNullable,
      Option(rs.getString("SCOPE_CATALOG")),
      Option(rs.getString("SCOPE_SCHEMA")),
      Option(rs.getString("SCOPE_TABLE")),
      Option(rs.getString("SOURCE_DATA_TYPE"))
    )
  }
}
