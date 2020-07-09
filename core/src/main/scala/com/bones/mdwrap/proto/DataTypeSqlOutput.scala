package com.bones.mdwrap.proto


abstract class DataTypeSqlOutput extends DataTypeOutput[String] {

  def toCase(string: String): String

  override def boolean: String = toCase("boolean")

  override def protoColumnOutput(protoColumn: ProtoColumn): String = {
    s"${protoColumn.name} ${dataTypeOutput(protoColumn.dataType)}"
  }

  override def binary(binaryType: BinaryType): String =
    binaryType.size match {
      case Some(s) => s"varbinary($s)"
      case None => "varbinary"
    }

  override def fixedLengthBinary(binaryType: FixedLengthBinaryType): String = {
    s"binary (${binaryType.size})"
  }

  override def fixedLengthCharacter(fixedLengthCharacterType: FixedLengthCharacterType): String =
    toCase(s"character(${fixedLengthCharacterType.length})")

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
    if (timestampType.withTimeZone) toCase("time with time zone")
    else toCase("time without time zone")


}
