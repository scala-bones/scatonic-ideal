package com.bones.si

import java.nio.charset.{Charset, StandardCharsets}

package object proto {

  sealed trait ProtoDataType
  object  BinaryType {
    def apply(size: Int): BinaryType = BinaryType(Some(size))
    def apply(): BinaryType = BinaryType(None)
  }
  case class BinaryType(size: Option[Int]) extends ProtoDataType
  case object BooleanType extends ProtoDataType
  case class FixedLengthBinaryType(size: Int) extends ProtoDataType
  case class FixedLengthCharacterType(length: Int, charset: Charset) extends ProtoDataType // NON-Varying type
  case object DateType extends ProtoDataType
  case object DoubleType extends ProtoDataType

  object IntegerType {
    def apply(): IntegerType = IntegerType(false)
    def autoIncrement: IntegerType = IntegerType(true)
  }
  case class IntegerType(autoIncrement: Boolean) extends ProtoDataType
  case object IntervalType extends ProtoDataType

  object LongType {
    def apply(): LongType = LongType(false)
    def autoIncrement: LongType = LongType(true)
  }
  case class LongType(autoIncrement: Boolean) extends ProtoDataType
  case class NumericType(precision: Int, scale: Int) extends ProtoDataType
  case object RealType extends ProtoDataType
  case object SmallIntType extends ProtoDataType

  object TimeType {
    def withoutTimeZone(): TimeType = TimeType(false)
    def withTimeZone(): TimeType = TimeType(true)
  }
  case class TimeType(withTimeZone: Boolean) extends ProtoDataType
  object TimestampType {
    def withoutTimeZone(): TimestampType = TimestampType(false)
    def withTimeZone(): TimestampType = TimestampType(true)
  }
  case class TimestampType(withTimeZone: Boolean) extends ProtoDataType

  object StringType {
    def apply(): StringType = StringType(None, StandardCharsets.UTF_8)
    def apply(length: Int): StringType = StringType(Some(length), StandardCharsets.UTF_8)
  }
  case class StringType(length: Option[Int], charset: Charset) extends ProtoDataType

  case class ProtoColumn(name: String, dataType: ProtoDataType, nullable: Boolean, remark: Option[String])
  case class ProtoForeignKey(column: ProtoColumn, foreignReference: (ProtoTable, ProtoColumn))
  case class ProtoTable(name: String, columns: List[ProtoColumn], primaryKeyColumns: List[ProtoColumn], foreignKeys: List[ProtoForeignKey], remark: Option[String]) {
    /** combines columns, primary key columns and foreign key columns */
    def allColumns: List[ProtoColumn] = primaryKeyColumns ::: columns ::: foreignKeys.map(_.column)
  }
  case class ProtoSchema(name: String, tables: List[ProtoTable])

}
