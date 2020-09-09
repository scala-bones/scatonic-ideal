package com.bones.si.jdbc.load.integration

import com.bones.si.jdbc.load.{DatabaseQuery, LoadColumn}
import com.bones.si.jdbc.{DataType, Nullable, YesNo}
import org.scalatest.matchers.must.Matchers

class LoadColumnTest extends IntegrationFixture with Matchers {

  test("load public columns") { f =>
    val query = DatabaseQuery.everything.schemas("public").catalogs("test")

    val result = LoadColumn.load(query, f.con)
    val a = result.filter(_.tableName == "wrapper_table_a").toArray
    val b = result.filter(_.tableName == "wrapper_table_b").toArray
    val ar = result.filter(_.tableName == "b").toArray


    a(0).catalogName mustEqual None
    a(0).schemaName mustEqual Some("public")
    a(0).tableName mustEqual "wrapper_table_a"
    a(0).name mustEqual "id"
    a(0).dataType mustEqual DataType.Integer
    a(0).typeName mustEqual "serial"
    a(0).columnSize mustEqual 10
    a(0).decimalDigits mustEqual Some(0)
    a(0).remarks mustEqual None
    a(0).columnDefault mustEqual Some("nextval('wrapper_table_a_id_seq'::regclass)")
    a(0).characterOctetLength mustEqual 10
    a(0).ordinalPosition mustEqual 1
    a(0).isNullable mustEqual YesNo.No
    a(0).sourceDataType mustEqual Some(0)
    a(0).isAutoIncrement mustEqual YesNo.Yes
    a(0).isGeneratedColumn mustEqual YesNo.Unknown
    a(0).nullable mustEqual Nullable.ColumnNoNulls

    a(1).dataType mustEqual DataType.BigInt
    a(1).typeName mustEqual "bigserial"

    a(2).dataType mustEqual DataType.Bit
    a(2).typeName mustEqual "bit"
    a(2).columnSize mustEqual 1

    a(3).dataType mustEqual DataType.Bit
    a(3).typeName mustEqual "bit"
    a(3).columnSize mustEqual 5
    a(3).isNullable mustEqual YesNo.Yes
    a(3).nullable mustEqual Nullable.ColumnNullable


    a(4).dataType mustEqual DataType.VarChar
    a(4).typeName mustEqual "text"
    a(4).columnSize mustEqual 2147483647

    a(5).dataType mustEqual DataType.Char
    a(5).typeName mustEqual "bpchar"
    a(5).columnSize mustEqual 1

    a(6).dataType mustEqual DataType.VarChar
    a(6).typeName mustEqual "varchar"
    a(6).columnSize mustEqual 255

    a(7).dataType mustEqual DataType.Date
    a(7).typeName mustEqual "date"

    a(8).dataType mustEqual DataType.Double
    a(8).typeName mustEqual "float8"

    a(9).dataType mustEqual DataType.Integer
    a(9).typeName mustEqual "int4"

    a(10).dataType mustEqual DataType.Numeric
    a(10).typeName mustEqual "numeric"

    a(11).dataType mustEqual DataType.Real
    a(11).typeName mustEqual "float4"

    a(12).dataType mustEqual DataType.SmallInt
    a(12).typeName mustEqual "int2"

    a(13).dataType mustEqual DataType.VarChar
    a(13).typeName mustEqual "text"

    a(14).dataType mustEqual DataType.Time
    a(14).typeName mustEqual "time"

    a(15).dataType mustEqual DataType.Time
    a(15).typeName mustEqual "timetz"

    a(16).dataType mustEqual DataType.Timestamp
    a(16).typeName mustEqual "timestamp"

    a(17).dataType mustEqual DataType.Timestamp
    a(17).typeName mustEqual "timestamptz"

    a(18).dataType mustEqual DataType.SqlXml
    a(18).typeName mustEqual "xml"

    b(0).dataType mustEqual DataType.Integer
    b(0).typeName mustEqual "serial"

    b(1).dataType mustEqual DataType.Integer
    b(1).typeName mustEqual "int4"


  }
}
