package com.bones.si

import java.sql.{DatabaseMetaData, Types}

/**
 * Case class wrappers for the types returned by the JDBC Database Metadata object.
 */
package object jdbc {

  case class DbAttribute(
    catalogName: Option[String],
    schemaName: Option[String],
    typeName: String,
    attributeName: String,
    dataType: DataType.Value,
    attributeTypeName: String,
    attributeSize: Int,
    decimalDigits: Option[Int],
    radix: Int,
    nullable: Nullable.Value,
    remarks: Option[String],
    defaultValue: Option[String],
    characterOctetLength: Option[Int],
    ordinalPotion: Int,
    isNullable: YesNo.Value,
    scopeCatalog: Option[String],
    scopeSchema: Option[String],
    scopeTable: Option[String],
    sourceDataType: Option[String])
  object Catalog {
    val catalogColumnName = "TABLE_CAT"
  }
  case class Catalog(name: String)
  object UpdateDeleteRule extends Enumeration {
    case class Val protected (name: String, intId: Int) extends super.Val
    implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

    def findById(id: Int) = values.toList.find(_.id == id)

    val ImportedNoAction =
      Val("ImportedKeyNoAction", DatabaseMetaData.importedKeyNoAction)
    val ImportedKeyCascade =
      Val("ImportedKeyCascade", DatabaseMetaData.importedKeyCascade)
    val ImportedKeySetNull =
      Val("ImportedKeySetNull", DatabaseMetaData.importedKeySetNull)
    val ImportedKeySetDefault =
      Val("ImportedKeySetDefault", DatabaseMetaData.importedKeySetDefault)
    val ImportedKeyRestrict =
      Val("ImportedKeyRestrict", DatabaseMetaData.importedKeyRestrict)
  }
  object Deferrability extends Enumeration {
    case class Val protected (name: String, intId: Int) extends super.Val
    implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]
    def findById(id: Int) = {
      values.toList.find(_.intId == id)
    }

    val ImportedKeyInitiallyDeferred =
      Val("ImportedKeyInitiallyDeferred", DatabaseMetaData.importedKeyInitiallyDeferred)
    val ImportedKeyInitiallyImmediate =
      Val("ImportedKeyInitiallyImmediate", DatabaseMetaData.importedKeyInitiallyImmediate)
    val ImportedKeyNotDeferrable =
      Val("ImportedKeyNotDeferrable", DatabaseMetaData.importedKeyNotDeferrable)

  }
  case class CrossReference(
    pkColumnCatalogName: Option[String],
    pkColumnSchemaName: Option[String],
    pkColumnTableName: String,
    pkColumnName: String,
    foreignCatalogName: Option[String],
    foreignSchemaName: Option[String],
    foreignTableName: String,
    foreignColumnName: String,
    keySequence: Short,
    updateRule: UpdateDeleteRule.Value,
    deleteRule: UpdateDeleteRule.Value,
    foreignKeyName: Option[String],
    primaryKeyName: Option[String],
    deferrability: Deferrability.Value)
  object DataType extends Enumeration {
    protected case class Val(name: String, intId: Int) extends super.Val
    def findByConstant(typeId: Int) = values.find(_.intId == typeId)
    implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

    val Bit = Val("Bit", Types.BIT)
    val TinyInt = Val("TinyInt", Types.TINYINT)
    val SmallInt = Val("SmallInt", Types.SMALLINT)
    val Integer = Val("Integer", Types.INTEGER)
    val BigInt = Val("BigInt", Types.BIGINT)
    val Float = Val("Float", Types.FLOAT)
    val Real = Val("Real", Types.REAL)
    val Double = Val("Double", Types.DOUBLE)
    val Numeric = Val("Numeric", Types.NUMERIC)
    val Decimal = Val("Decimal", Types.DECIMAL)
    val Char = Val("Char", Types.CHAR)
    val VarChar = Val("VarChar", Types.VARCHAR)
    val LongVarChar = Val("LongVarChar", Types.LONGVARCHAR)
    val Date = Val("Date", Types.DATE)
    val Time = Val("Time", Types.TIME)
    val Timestamp = Val("Timestamp", Types.TIMESTAMP)
    val Binary = Val("Binary", Types.BINARY)
    val VarBinary = Val("VarBinary", Types.VARBINARY)
    val Null = Val("Null", Types.NULL)
    val Other = Val("Other", Types.OTHER)
    val JavaObject = Val("JavaObject", Types.JAVA_OBJECT)
    val Distinct = Val("Distinct", Types.DISTINCT)
    val Struct = Val("Struct", Types.STRUCT)
    val Array = Val("Array", Types.ARRAY)
    val Blob = Val("Blob", Types.BLOB)
    val Clob = Val("Clob", Types.CLOB)
    val Ref = Val("Ref", Types.REF)
    val DataLink = Val("DataLink", Types.DATALINK)
    val Boolean = Val("Boolean", Types.BOOLEAN)
    val RowId = Val("RowId", Types.ROWID)
    val NChar = Val("NChar", Types.NCHAR)
    val NVarChar = Val("NVarChar", Types.NVARCHAR)
    val LongNVarChar = Val("LongNVarChar", Types.LONGNVARCHAR)
    val NClob = Val("NClob", Types.NCLOB)
    val SqlXml = Val("SqlXml", Types.SQLXML)
    val RefCursor = Val("RefCursor", Types.REF_CURSOR)
    val TimeWithTimeZone = Val("TimeWithTimeZone", Types.TIME_WITH_TIMEZONE)
    val TimestampWithTimeZone =
      Val("TimestampWithTimeZone", Types.TIMESTAMP_WITH_TIMEZONE)
  }
  object Nullable extends Enumeration {
    case class Val protected (name: String, intId: Int) extends super.Val
    implicit def valueToNullableVal(x: Value): Val = x.asInstanceOf[Val]

    def findById(id: Int) = values.toList.find(_.intId == id)
    val ColumnNoNulls = Val("ColumnNoNulls", DatabaseMetaData.columnNoNulls)
    val ColumnNullable = Val("ColumnNullable", DatabaseMetaData.columnNullable)
    val ColumnNullableUnknown =
      Val("ColumnNullableUnknown", DatabaseMetaData.columnNullableUnknown)
  }
  object YesNo extends Enumeration {
    def fromBoolean(b: Boolean) = if (b) Yes else No
    def findByString(str: String) = {
      if (str.equalsIgnoreCase("yes")) Some(Yes)
      else if (str.equalsIgnoreCase("no")) Some(No)
      else if (str.isEmpty) Some(Unknown)
      else None
    }
    def findByOptionalString(opt: Option[String]): Option[Value] = opt match {
      case Some(str) => findByString(str)
      case None      => Some(Unknown)
    }
    type YesNo = Value
    val Yes, No, Unknown = Value
  }

  object Column {
    val categoryNameCol = "TABLE_CAT"
    val schemaNameCol = "TABLE_SCHEM"
    val tableNameCol = "TABLE_NAME"
    val nameCol = "COLUMN_NAME"
    val dataTypeCol = "DATA_TYPE"
    val typeNameCol = "TYPE_NAME"
    val columnSizeCol = "COLUMN_SIZE"
    val decimalDigitsCol = "DECIMAL_DIGITS"
    val numProcRadixCol = "NUM_PREC_RADIX"
    val nullableCol = "NULLABLE"
    val remarksCol = "REMARKS"
    val columnDefaultCol = "COLUMN_DEF"
    val characterOctetLengthCol = "CHAR_OCTET_LENGTH"
    val ordinalPositionCol = "ORDINAL_POSITION"
    val isNullableCol = "IS_NULLABLE"
    val scopeCatalogCol = "SCOPE_CATALOG"
    val scopeSchemaCol = "SCOPE_SCHEMA"
    val scopeTableCol = "SCOPE_TABLE"
    val sourceDataTypeCol = "SOURCE_DATA_TYPE"
    val isAutoIncrementCol = "IS_AUTOINCREMENT"
    val isGeneratedColumnCol = "IS_GENERATEDCOLUMN"

  }
  case class Column(
    catalogName: Option[String],
    schemaName: Option[String],
    tableName: String,
    name: String,
    dataType: DataType.Value,
    typeName: String,
    columnSize: Int,
    decimalDigits: Option[Int],
    numPrecRadix: Int,
    nullable: Nullable.Value,
    remarks: Option[String],
    columnDefault: Option[String],
    characterOctetLength: Int,
    ordinalPosition: Int,
    isNullable: YesNo.YesNo,
    sourceDataType: Option[Short],
    isAutoIncrement: YesNo.YesNo,
    isGeneratedColumn: YesNo.YesNo)

  object FunctionType extends Enumeration {
    case class Val protected (typeId: Int) extends super.Val
    implicit def valueToNullableVal(x: Value): Val = x.asInstanceOf[Val]
    def findById(typeId: Int) =
      values.toList.find(_.typeId == typeId)

    val FunctionResultUnknown = Val(0)
    val FunctionNoTable = Val(1)
    val FunctionReturnsTable = Val(2)
  }
  case class Function(
    catalogName: Option[String],
    schemaName: Option[String],
    functionName: String,
    remarks: Option[String],
    functionType: FunctionType.Value,
    specificName: String)

  case class ImportedKeys(
    primaryKeyTableCatalogName: Option[String],
    primaryKeyTableSchemaName: Option[String],
    primaryKeyTableName: String,
    primaryKeyColumnName: String,
    foreignKeyTableCatalogName: Option[String],
    foreignKeyTableSchemaName: Option[String],
    foreignKeyTableName: String,
    foreignKeyColumnName: String,
    keySequence: Short,
    updateRule: UpdateDeleteRule.Value,
    deleteRule: UpdateDeleteRule.Value,
    foreignKeyName: Option[String],
    primaryKeyName: Option[String],
    deferrability: Deferrability.Value)
  case class IndexInfo()
  object TableType extends Enumeration {
    type TableType = Value
    case class Val protected (name: String) extends super.Val
    implicit def valueToNullableVal(x: Value): Val = x.asInstanceOf[Val]

    def findByStr(str: String): Either[String, Value] =
      values.toList.find(_.name.equalsIgnoreCase(str)).toRight(str)
    val Table = Val("TABLE")
    val View = Val("VIEW")
    val SystemTable = Val("SYSTEM TABLE")
    val GlobalTemporary = Val("GLOBAL TEMPORARY")
    val LocalTemporary = Val("LOCAL TEMPORARY")
    val Alias = Val("ALIAS")
    val Synonym = Val("SYNONYM")
    val Index = Val("INDEX")
    val Sequence = Val("SEQUENCE")
  }
  object ReferenceGeneration extends Enumeration {
    type ReverenceGeneration = Value
    case class Val protected (name: String) extends super.Val
    def findByString(str: String) = values.find(_.name == str)
    implicit def valueToNullableVal(x: Value): Val = x.asInstanceOf[Val]
    val System = Val("SYSTEM")
    val User = Val("USER")
    val Derived = Val("DERIVED")
  }
  case class Table(
    catalogName: Option[String],
    schemaName: Option[String],
    name: String,
    tableType: Either[String, TableType.Value],
    remarks: Option[String],
    typesCatalog: Option[String],
    typesSchema: Option[String],
    typeName: Option[String],
    selfReferencingColumnName: Option[String],
    referenceGeneration: Option[ReferenceGeneration.Value])
  case class TablePrivilege(
    catalogName: Option[String],
    schemaName: Option[String],
    name: String,
    grantor: Option[String],
    grantee: String,
    privilege: String,
    isGrantable: YesNo.Value)

  object Searchable extends Enumeration {
    case class Val(searchableId: Int, name: String, description: String) extends super.Val
    implicit def valueToNullableVal(x: Value): Val = x.asInstanceOf[Val]
    def findBySearchableId(searchableId: Int): Option[Value] =
      values.find(_.searchableId == searchableId)

    val TypePredNone = Val(DatabaseMetaData.typePredNone, "typePredNone", "No support")
    val TypePredChar =
      Val(DatabaseMetaData.typePredChar, "typePredChar", "Only supported with WHERE .. LIKE")
    val typePredBasic =
      Val(DatabaseMetaData.typePredBasic, "typePredBasic", "Supported except for WHERE .. LIKE")
    val typeSearchable =
      Val(DatabaseMetaData.typeSearchable, "typeSearchable", "Supported for all WHERE ..")
  }
  case class TypeInfo(
    typeName: String,
    dataType: DataType.Value,
    precision: Int,
    literalPrefix: Option[String],
    literalSuffix: Option[String],
    createParameters: Option[String],
    nullable: Nullable.Value,
    caseSensitive: Boolean,
    searchable: Searchable.Value,
    isUnsigned: Boolean,
    fixedPrecisionScale: Boolean,
    autoIncrement: Boolean,
    localTypeName: Option[String],
    minimumScale: Short,
    maximumScale: Short,
    numericalPrecistionRadix: Int
  )
  case class PrimaryKey(
    catalogName: Option[String],
    schemaName: Option[String],
    tableName: String,
    columnName: String,
    keySequence: Short,
    name: Option[String])

  object ProcedureType extends Enumeration {
    case class Val(procedureTypeId: Int, name: String, description: String) extends super.Val
    implicit def valueToNullableVal(x: Value): Val = x.asInstanceOf[Val]
    def findByProcedureTypeId(procedureTypeId: Int): Option[Value] =
      values.find(_.procedureTypeId == procedureTypeId)

    val ProcedureResultUnknown = Val(DatabaseMetaData.procedureResultUnknown, "procedureResultUnknown", "Cannot determine if  a return value will be returned")
    val ProcedureNoResult = Val(DatabaseMetaData.procedureNoResult, "procedureNoResult", "Does not return a return value")
    val ProcedureReturnsResult = Val(DatabaseMetaData.procedureReturnsResult, "procedureReturnsResult", "Returns a return value")

  }
  case class Procedure(
    catalogName: Option[String],
    schemaName: Option[String],
    name: String,
    remarks: Option[String],
    procedureType: ProcedureType.Value,
    specificName: String)

  case class Schema(name: String, tables: List[Table])
}
