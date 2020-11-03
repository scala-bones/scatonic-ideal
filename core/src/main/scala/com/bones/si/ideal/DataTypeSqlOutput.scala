package com.bones.si.ideal

/**
  * Implementation of an output which is an SQL String.
  */
abstract class DataTypeSqlOutput extends DataTypeOutput[String] {

  def toCase(string: String): String

  private val NOT_NULL = toCase("not null")
  private val VARBINARY = toCase("varbinary")
  private val BINARY = toCase("binary")
  private val CHARACTER = toCase("character")

  override def boolean: String = toCase("boolean")

  override def protoColumnOutput(protoColumn: IdealColumn): String = {
    val nullableStr = if (protoColumn.nullable) "" else " " + NOT_NULL
    s"${protoColumn.name} ${dataTypeOutput(protoColumn.dataType)}${nullableStr}"
  }

  override def binary(binaryType: BinaryType): String =
    binaryType.size match {
      case Some(s) => s"$VARBINARY($s)"
      case None    => s"$VARBINARY"
    }

  override def fixedLengthBinary(binaryType: FixedLengthBinaryType): String = {
    s"$BINARY (${binaryType.size})"
  }

  override def fixedLengthCharacter(fixedLengthCharacterType: FixedLengthCharacterType): String =
    toCase(s"$CHARACTER(${fixedLengthCharacterType.length})")

  override def date: String = toCase("date")

  override def double: String = toCase("double precision")

  override def integer(integerType: IntegerType): String =
    toCase("integer")

  override def interval: String = toCase("interval")

  override def long(longType: LongType): String =
    toCase("bigint")

  override def numeric(numericType: NumericType): String =
    toCase(s"numeric(${numericType.precision},${numericType.scale})")

  override def real: String =
    toCase("real")

  override def smallInt: String =
    toCase("smallint")

  override def time(timeType: TimeType): String =
    if (timeType.withTimeZone) toCase("time with time zone")
    else toCase("time without time zone")

  override def timestamp(timestampType: TimestampType): String =
    if (timestampType.withTimeZone) toCase("timestamp with time zone")
    else toCase("timestamp without time zone")

}
