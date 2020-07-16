package com.bones.si.proto

case class PostgresSqlOutput(toCaseFunction: String => String) extends DataTypeSqlOutput {

  override def toCase(string: String): String = toCaseFunction(string)

  override def binary(binaryType: BinaryType): String =
    if (binaryType.size.contains(1)) toCase("bit") else toCase("bytea")


  override def fixedLengthBinary(binaryType: FixedLengthBinaryType): String =
    if (binaryType.size == 1) toCase("bit") else toCase("bytea")

  override def integer(integerType: IntegerType): String =
    if (integerType.autoIncrement) toCase("serial") else toCase("integer")

  override def long(longType: LongType): String =
    if (longType.autoIncrement) toCase("bigserial") else toCase("bigint")

  override def string(stringType: StringType): String =
    stringType.length.filter(_ < 255) match {
      case Some(l) => toCase(s"varchar($l)")
      case None => toCase("text")
    }

}
