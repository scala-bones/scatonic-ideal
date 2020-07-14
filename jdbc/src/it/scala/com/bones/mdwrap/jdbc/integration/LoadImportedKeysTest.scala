package com.bones.mdwrap.jdbc.integration

import java.sql.Connection

import com.bones.mdwrap.{DatabaseQuery, Deferrability, UpdateDeleteRule}
import com.bones.mdwrap.jdbc.LoadImportedKeys
import org.scalatest.matchers.must.Matchers

class LoadImportedKeysTest  extends IntegrationFixture with Matchers {

  test("load imported keys") { f=>
    val query = DatabaseQuery.everything
    val importedKeysMd = LoadImportedKeys.load(query, f.con)

    val importedKeys = importedKeysMd.filter(_.primaryKeyTableName == "wrapper_table_a")
    importedKeys(0).primaryKeyTableCatalogName mustEqual None
    importedKeys(0).primaryKeyTableSchemaName mustEqual Some("public")
    importedKeys(0).primaryKeyTableName mustEqual "wrapper_table_a"
    importedKeys(0).primaryKeyColumnName mustEqual "id"
    importedKeys(0).foreignKeyTableCatalogName mustEqual None
    importedKeys(0).foreignKeyTableSchemaName mustEqual Some("public")
    importedKeys(0).foreignKeyColumnName mustEqual "table_a_id"
    importedKeys(0).keySequence mustEqual 1
    importedKeys(0).updateRule mustEqual UpdateDeleteRule.ImportedKeySetDefault
    importedKeys(0).deleteRule mustEqual UpdateDeleteRule.ImportedKeySetDefault
    importedKeys(0).foreignKeyName mustEqual Some("wrapper_table_b_table_a_id_fkey")
    importedKeys(0).primaryKeyName mustEqual Some("wrapper_table_a_id_key")
    importedKeys(0).deferrability mustEqual Deferrability.ImportedKeyNotDeferrable

  }

}
