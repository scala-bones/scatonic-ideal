package com.bones.mdwrap.jdbc.integration

import com.bones.mdwrap.{DataType, Nullable, Searchable, TypeInfo}
import com.bones.mdwrap.jdbc.LoadTypeInfo
import org.scalatest.matchers.must.Matchers

class LoadTypeInfoTest extends IntegrationFixture with Matchers {

  test("load type info") { f =>
    val typeInfo = LoadTypeInfo.loadAll(f.con)

    typeInfo

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
      10)
  }
}
