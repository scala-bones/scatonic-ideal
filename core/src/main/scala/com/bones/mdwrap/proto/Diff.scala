package com.bones.mdwrap.proto

import com.bones.mdwrap.{Column, DataType, DatabaseCache, Nullable, Table, YesNo}

/**
  * Diff the sub DB representation to what is in the Database Cache.
  */
object Diff {

  trait ColumnDiff
  case class ColumnDataTypeDiff(existingDataType: DataType.Value, newDataType: ProtoDataType)
      extends ColumnDiff
  case class ColumnRemarkDiff(existingRemark: Option[String], newRemark: Option[String])
      extends ColumnDiff
  case class ColumnNullableDiff(existingNullable: YesNo.Value, newNullable: YesNo.Value)
      extends ColumnDiff

  /**
    * Give a database Cache and a Schema Prototype, find the list of changes needed to be made
    * to the Database to be in sync with the Prototype.  This will only find adds and updates to the database.
    * This will not report any structures in the existing database cache that is not part of the Schema Prototype.
    * @param databaseCache The Database Cache for comparison.
    * @param protoSchema The schema prototype -- what structures we want in our database.
    * @return Tuple5 with the differences.
    *         _1 = List of tables in the prototype which are not in the cache
    *         _2 = List of columns in the prototype which are not in the cache
    *         _3 = List of columns that are different in the prototype than in the cache
    *         _4 = List of primary keys in the prototype which are not in the cache
    *         _5 = List of foreign keys in the prototype which are not in the cache
    */
  def findDiff(databaseCache: DatabaseCache, protoSchema: ProtoSchema): (
    List[ProtoTable],
    List[ProtoColumn],
    List[(ProtoColumn, List[ColumnDiff])],
    List[ProtoPrimaryKey],
    List[ProtoForeignKey]) = {
    val (missingTables, existingTables) =
      missingVsExistingTables(databaseCache, protoSchema.name, protoSchema.tables)
    val missingExistingColumns =
      existingTables.foldLeft((List.empty[ProtoColumn], List.empty[(ProtoColumn, Column)])) {
        (result, table) =>
          {
            val missingExisting = missingVsExistingColumns(databaseCache, table._1, table._2)
            result
              .copy(_1 = result._1 ::: missingExisting._1)
              .copy(_2 = result._2 ::: missingExisting._2)
          }
      }

    val columnDiff = missingExistingColumns._2.flatMap { c =>
      val diff = compareColumn(c._1, c._2)
      if (diff.isEmpty) None
      else Some((c._1, diff))
    }

    (
      missingTables,
      missingExistingColumns._1,
      columnDiff,
      List.empty[ProtoPrimaryKey],
      List.empty[ProtoForeignKey])

  }

  /**
    * Traverses through the table prototypes and tries to find a table in the cache with same same
    * name and in the same schema.
    * @param databaseCache The cache, to look up tables.
    * @param schemaName The name of the prototype schema
    * @param tables The list of prototype tables we are looking up in the cache
    * @return Tuple2 where _1 is the list of missing tables and _2 is the
   *         list of pair of the prototype table and the cached table.
    */
  def missingVsExistingTables(
    databaseCache: DatabaseCache,
    schemaName: String,
    tables: List[ProtoTable]): (List[ProtoTable], List[(ProtoTable, Table)]) = {
    tables.foldRight((List.empty[ProtoTable], List.empty[(ProtoTable, Table)])) {
      (subTable, result) =>
        {
          databaseCache.findTableByName(schemaName, subTable.name) match {
            case Some(table) => result.copy(_2 = (subTable, table) :: result._2)
            case None        => result.copy(_1 = subTable :: result._1)
          }
        }
    }
  }

  /**
   * Compare the two tables and try to find a cached column which matches each of the columns in the protoype table.
   * @param databaseCache The cache used for lookup.
   * @param table The table prototype, what we want the table to look like
   * @param diffTable The cached table
   * @return Pair of List where _1 is the List of columns not found in the cache and _2 is the pair of matching prototype/existing columns
   */
  def missingVsExistingColumns(
    databaseCache: DatabaseCache,
    table: ProtoTable,
    diffTable: Table): (List[ProtoColumn], List[(ProtoColumn, Column)]) = {
    table.columns.foldLeft((List.empty[ProtoColumn], List.empty[(ProtoColumn, Column)])) {
      (result, protoColumn) =>
        {
          databaseCache.findColumnByName(diffTable, protoColumn.name) match {
            case None    => result.copy(_1 = protoColumn :: result._1)
            case Some(c) => result.copy(_2 = (protoColumn, c) :: result._2)
          }
        }
    }
  }

  /**
   * Compares two columns for differences, currently including data type, remarks or nullable.
   * @param column The column prototype
   * @param diffColumn The cached column for comparison
   * @return List of differences
   */
  def compareColumn(column: ProtoColumn, diffColumn: Column): List[ColumnDiff] = {
    val dt =
      if (! isEquivalent(column.dataType, diffColumn.dataType, diffColumn))
        List(ColumnDataTypeDiff(diffColumn.dataType, column.dataType))
      else List.empty
    val rm =
      if (column.remark != diffColumn.remarks)
        List(ColumnRemarkDiff(diffColumn.remarks, column.remark))
      else List.empty
    val nl =
      if (diffColumn.isNullable == YesNo.Unknown ||
          (column.nullable && diffColumn.isNullable == YesNo.No) ||
          (!column.nullable && diffColumn.isNullable == YesNo.Yes))
        List(ColumnNullableDiff(diffColumn.isNullable, YesNo.fromBoolean(column.nullable)))
      else List.empty

    dt ::: rm ::: nl ::: Nil
  }

  def isEquivalent(protoDataType: ProtoDataType, dataType: DataType.Value, column: Column): Boolean = {
    (protoDataType, dataType) match {
      case (BinaryType(size), DataType.Bit) if size.contains(1) => true
      case (SmallIntType, DataType.TinyInt) => true
      case (SmallIntType, DataType.SmallInt) => true
      case (IntegerType(_), DataType.Integer) => true
      case (LongType(_), DataType.BigInt) => true
      case (RealType, DataType.Float) => true
      case (RealType, DataType.Real) => true
      case (DoubleType, DataType.Double) => true
      case (NumericType(_,_), DataType.Numeric) => true //TODO: Check if precision is ok
      case (NumericType(_,_), DataType.Decimal) => true //TODO: Check if precision is ok
      case (StringType(sz, charset), DataType.VarChar) => true //TODO: check fixed rate
      case (StringType(sz, charset), DataType.LongVarChar) => true
      case (DateType, DataType.Date) => true
      case (TimeType(tz), DataType.Time) if !tz => true
      case (TimestampType(tz), DataType.Timestamp) if !tz => true
      case (BinaryType(size), DataType.VarBinary) => true //TODO: check size
      case (FixedLengthBinaryType(size), DataType.Binary) => true //TODO: check size
      case (BinaryType(size), DataType.Blob) => true //TODO: check size
      case (StringType(size,charset), DataType.Clob) => true  //TODO: check size
      case (BooleanType, DataType.Boolean) => true
      case (FixedLengthCharacterType(size, charset), DataType.NChar) => true
      case (StringType(size, charset), DataType.NVarChar) => true
      case (StringType(size, charset), DataType.LongNVarChar) => true
      case (StringType(size, charset), DataType.NClob) => true
      case (TimeType(tz), DataType.TimeWithTimeZone) if tz => true
      case (TimestampType(tz), DataType.TimestampWithTimeZone) if tz => true
      case _ => false
    }

  }

}
