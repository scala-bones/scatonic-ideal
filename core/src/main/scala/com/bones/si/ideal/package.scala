package com.bones.si

import java.nio.charset.{Charset, StandardCharsets}

/**
 * Contains the class and object to create the ideal database schema in memory.
 */
package object ideal {

  sealed trait IdealDataType
  case class ArrayType(arrayOf: IdealDataType) extends IdealDataType
  object  BinaryType {
    def apply(size: Int): BinaryType = BinaryType(Some(size))
    val unbounded: BinaryType = BinaryType(None)
  }
  case class BinaryType(size: Option[Int]) extends IdealDataType
  case object BooleanType extends IdealDataType
  case class FixedLengthBinaryType(size: Int) extends IdealDataType
  case class FixedLengthCharacterType(length: Int, charset: Charset) extends IdealDataType // NON-Varying type
  case object DateType extends IdealDataType
  case object DoubleType extends IdealDataType

  object IntegerType {
    def apply(): IntegerType = IntegerType(false)
    def autoIncrement: IntegerType = IntegerType(true)
  }
  case class IntegerType(autoIncrement: Boolean) extends IdealDataType
  case object IntervalType extends IdealDataType

  object LongType {
    def apply(): LongType = LongType(false)
    def autoIncrement: LongType = LongType(true)
  }
  case class LongType(autoIncrement: Boolean) extends IdealDataType
  case class NumericType(precision: Int, scale: Int) extends IdealDataType
  case object RealType extends IdealDataType
  case object SmallIntType extends IdealDataType

  object TimeType {
    def withoutTimeZone(): TimeType = TimeType(false)
    def withTimeZone(): TimeType = TimeType(true)
  }
  case class TimeType(withTimeZone: Boolean) extends IdealDataType
  object TimestampType {
    def withoutTimeZone(): TimestampType = TimestampType(false)
    def withTimeZone(): TimestampType = TimestampType(true)
  }
  case class TimestampType(withTimeZone: Boolean) extends IdealDataType

  object StringType {
    val unbounded: StringType = StringType(None, StandardCharsets.UTF_8)
    def apply(length: Int): StringType = StringType(Some(length), StandardCharsets.UTF_8)
  }
  case class StringType(length: Option[Int], charset: Charset) extends IdealDataType

  /**
   * Represents the 'ideal' column.
   * @param name Column Name
   * @param dataType Column Data Type
   * @param nullable is the data nullable?
   * @param remark Comment about the column
   */
  case class IdealColumn(name: String, dataType: IdealDataType, nullable: Boolean, remark: Option[String])

  /**
   * Represents an 'ideal' foreign key.
   * @param column A reference to the column this key is.
   * @param foreignReference Reference to the foreign key in a specific table.
   */
  case class IdealForeignKey(column: IdealColumn, foreignReference: (IdealTable, IdealColumn))

  /**
   * Represents an 'ideal' table.
   * @param name Name of the table.
   * @param columns A list of the columns in the table (excluding primary and foreign key)
   * @param primaryKeyColumns A list of the primary keys of this table.
   * @param foreignKeys A list of the foreign key constraints reference from this table.
   * @param remark A comment about the table.
   */
  case class IdealTable(name: String, primaryKeyColumns: List[IdealColumn], columns: List[IdealColumn], foreignKeys: List[IdealForeignKey], remark: Option[String]) {
    /** combines columns, primary key columns and foreign key columns */
    def allColumns: List[IdealColumn] = primaryKeyColumns ::: columns ::: foreignKeys.map(_.column)
  }
  object IdealTable {
    def apply(name: String, primaryKey: IdealColumn, columns: List[IdealColumn], foreignKeys: List[IdealForeignKey] = List.empty, remark: Option[String] = None): IdealTable =
      IdealTable(name, List(primaryKey), columns, foreignKeys, remark)
    def empty(name: String, description: Option[String] = None) = IdealTable(name, List.empty, List.empty, List.empty, description)
  }

  /**
   * Represents an 'ideal' Schema.
   * @param name The name of the schema.
   * @param tables The list of tables within this schema.
   */
  case class IdealSchema(name: String, tables: List[IdealTable])

}
