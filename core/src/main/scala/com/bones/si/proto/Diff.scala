package com.bones.si.proto

import com.bones.si._

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

  case class PrimaryKeyDiff(missing: List[ProtoColumn], extraneousKeys: List[PrimaryKey])

  case class DiffResult(tablesMissing: List[ProtoTable],
                        columnsMissing: List[(ProtoTable, ProtoColumn)],
                        columnsDifferent: List[(ProtoColumn, List[ColumnDiff])],
                        primaryKeysMissing: List[(ProtoTable, ProtoColumn)],
                        primaryKeysExtraneous: List[(ProtoTable, PrimaryKey)],
                        missingForeignKeys: List[ProtoForeignKey])

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
    *         _4 = List of primary keys that are expected in the table cache
   *          _5 = List of primary keys on extra on a table cache
    *         _6 = List of foreign keys in the prototype which are not in the cache
    */
  def findDiff(databaseCache: DatabaseCache, protoSchema: ProtoSchema): DiffResult = {
    val (missingTables, existingTables) =
      missingVsExistingTables(databaseCache, protoSchema.name, protoSchema.tables)
    val missingExistingColumns =
      existingTables.foldLeft(
        (List.empty[(ProtoTable, ProtoColumn)], List.empty[(ProtoColumn, Column)])) {
        (result, table) =>
          {
            val missingExisting = missingVsExistingColumns(databaseCache, table._1, table._2)
            val withTable = missingExisting._1.map(c => (table._1, c))
            result
              .copy(_1 = result._1 ::: withTable)
              .copy(_2 = result._2 ::: missingExisting._2)
          }
      }

    val columnDiff = missingExistingColumns._2.flatMap { c =>
      val diff = compareColumn(c._1, c._2)
      if (diff.isEmpty) None
      else Some((c._1, diff))
    }

    val primaryKeyDifference = existingTables.map(table => {
      val diff = findPrimaryKeyDifferences(databaseCache, protoSchema.name, table._1, table._2)
      ( diff.missing.map( (table._1, _)), diff.extraneousKeys.map( (table._1, _)))
    })

    DiffResult(
      missingTables,
      missingExistingColumns._1,
      columnDiff,
      primaryKeyDifference.flatMap(_._1),
      primaryKeyDifference.flatMap(_._2),
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
    table.allColumns.foldLeft((List.empty[ProtoColumn], List.empty[(ProtoColumn, Column)])) {
      (result, protoColumn) =>
        {
          databaseCache.findColumnByName(diffTable, protoColumn.name) match {
            case None    => result.copy(_1 = protoColumn :: result._1)
            case Some(c) => result.copy(_2 = (protoColumn, c) :: result._2)
          }
        }
    }
  }

  object PrimaryKeyDiff {
    def withExtraneous(extraneous: List[PrimaryKey]): PrimaryKeyDiff =
      PrimaryKeyDiff(List.empty, extraneous)
  }

  def findPrimaryKeyDifferences(
    databaseCache: DatabaseCache,
    schemaName: String,
    table: ProtoTable,
    diffTable: Table): PrimaryKeyDiff = {
    val tablePks = databaseCache.primaryKeys.filter(pk => pk.schemaName.contains(schemaName) && pk.tableName == table.name)
    //The list of Differences and remaining PrimaryKeys in the cache which
    // are not currently matched up with a ProtoColumn
    table.primaryKeyColumns.foldLeft(PrimaryKeyDiff.withExtraneous(tablePks)) {
      (result, nextColumn) =>
        {
          val diff = result
          val (matchingPks, remainingPks) = diff.extraneousKeys.partition(pk =>
            pk.columnName == nextColumn.name)
          matchingPks match {
            case _ :: Nil =>
              diff.copy(extraneousKeys = remainingPks)
            case _ :: xs =>
              //column matches more than one PK.  This shouldn't happen, so we'll use one and keep the rest in remaining
              diff.copy(extraneousKeys = xs ::: remainingPks)
            case _ =>
              diff.copy(missing = nextColumn :: diff.missing)
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
      if (!isEquivalent(column.dataType, diffColumn.dataType, diffColumn))
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

  /**
    * Goal is to determine if the JDBC type satisfies the specified DataType for the ProtoColumn
    * @param protoDataType
    * @param dataType
    * @param column
    * @return
    */
  def isEquivalent(
    protoDataType: ProtoDataType,
    dataType: DataType.Value,
    column: Column): Boolean = {
    (protoDataType, dataType) match {
      case (BinaryType(size), DataType.Bit) if size.contains(1) => true
      case (SmallIntType, DataType.TinyInt)                     => true
      case (SmallIntType, DataType.SmallInt)                    => true
      case (IntegerType(_), DataType.Integer)                   => true
      case (LongType(_), DataType.BigInt)                       => true
      case (RealType, DataType.Float)                           => true
      case (RealType, DataType.Real)                            => true
      case (DoubleType, DataType.Double)                        => true
      case (NumericType(p, s), DataType.Numeric) =>
        column.columnSize == p && column.decimalDigits.contains(s)
      case (NumericType(p, s), DataType.Decimal) =>
        column.columnSize == p && column.decimalDigits.contains(s)
      case (StringType(sz, _), DataType.VarChar) =>
        sz match {
          case Some(i) if i > 255 => column.columnSize >= i
          case Some(i)            => column.columnSize == i
          case None               => column.typeName != "varchar" //eg, postgres uses "text" for unlimited size
        }
      case (StringType(sz, charset), DataType.LongVarChar) =>
        sz match {
          case Some(i) if i > 255 => column.columnSize >= i
          case Some(i)            => column.columnSize == i
          case None               => true
        }
      case (DateType, DataType.Date)                      => true
      case (TimeType(tz), DataType.Time) if !tz           => true
      case (TimestampType(tz), DataType.Timestamp) if !tz => true
      case (BinaryType(size), DataType.VarBinary) =>
        size.forall(column.columnSize > _)
      case (FixedLengthBinaryType(size), DataType.Binary) =>
        column.columnSize == size
      case (BinaryType(size), DataType.Blob) =>
        size.forall(column.columnSize > _)
      case (StringType(size, _), DataType.Clob) =>
        size.forall(column.columnSize > _)
      case (BooleanType, DataType.Boolean)                           => true
      case (FixedLengthCharacterType(size, charset), DataType.NChar) => true
      case (StringType(size, _), DataType.NVarChar) =>
        size.forall(column.columnSize > _)
      case (StringType(size, _), DataType.LongNVarChar) =>
        size.forall(column.columnSize > _)
      case (StringType(size, _), DataType.NClob) =>
        size.forall(column.columnSize > _)
      case (TimeType(tz), DataType.TimeWithTimeZone) if tz           => true
      case (TimestampType(tz), DataType.TimestampWithTimeZone) if tz => true
      case _                                                         => false
    }

  }

}
