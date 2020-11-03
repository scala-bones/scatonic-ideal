package com.bones.si.jdbc.load.integration

import com.bones.si.jdbc.{AscDesc, IndexType}
import com.bones.si.jdbc.load.{DatabaseQuery, LoadIndexInfo}
import org.scalatest.matchers.must.Matchers

class LoadIndexInfoTest extends IntegrationFixture with Matchers {

  test("load index info") { f =>
    val query = DatabaseQuery.everything.tables("wrapper_table_a")
    val indexInfos = LoadIndexInfo.load(query, f.con)

    val indexInfo = indexInfos(0)

    indexInfo.tableCatalog mustBe None
    indexInfo.tableSchema mustBe Some("public")
    indexInfo.tableName mustBe "wrapper_table_a"
    indexInfo.nonUnique mustBe false
    indexInfo.indexQualifier mustBe None
    indexInfo.indexName mustBe "wrapper_table_a_big_id_key"
    indexInfo.indexType mustBe IndexType.tableIndexOther
    indexInfo.ordinalPosition mustBe 1
    indexInfo.columnName mustBe Some("big_id")
    indexInfo.ascOrDesc mustBe Some(AscDesc.asc)
    indexInfo.cardinality mustBe 0
    indexInfo.pages mustBe 1
    indexInfo.filterPosition mustBe None

    indexInfos.foreach(x => println(x.columnName + "|" + x.nonUnique + "|" + x.indexName))

  }
}
