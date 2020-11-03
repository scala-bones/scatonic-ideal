package com.bones.si.jdbc.load.integration

import com.bones.si.jdbc.load.LoadTypeInfo
import com.bones.si.jdbc.{DataType, Nullable, Searchable, TypeInfo}
import org.scalatest.matchers.must.Matchers

class LoadTypeInfoTest extends IntegrationFixture with Matchers {

  test("load type info") { f =>
    val typeInfo = LoadTypeInfo.loadAll(f.con)

    val varchar = typeInfo.find(t => t.typeName == "varchar").get
    varchar mustEqual TypeInfo(
      "varchar",
      DataType.VarChar,
      10485760,
      Some("'"),
      Some("'"),
      None,
      Nullable.ColumnNullable,
      true,
      Searchable.typeSearchable,
      true,
      false,
      false,
      None,
      0,
      0,
      10
    )
  }
}
