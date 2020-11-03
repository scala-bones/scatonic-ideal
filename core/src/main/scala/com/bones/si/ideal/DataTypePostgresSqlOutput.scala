package com.bones.si.ideal

/**
  * Produces output for types specific to Postgres, such as bytea, serial and text.
  * @param toCaseFunction Used as a post processor for SQL Keywords to produce a specifit output style, usually upper or lower case.
  */
case class DataTypePostgresSqlOutput(toCaseFunction: String => String) extends DataTypeSqlOutput {

  override def toCase(string: String): String = toCaseFunction(string)

  private val BIT = toCase("bit")
  private val BYTEA = toCase("bytea")
  private val SERIAL = toCase("serial")
  private val INTEGER = toCase("integer")
  private val BIGSERIAL = toCase("bigserial")
  private val BIGINT = toCase("bigint")
  private val VARCHAR = toCase("varchar")
  private val TEXT = toCase("text")

  override def array(arrayType: ArrayType): String = {
    s"${this.dataTypeOutput(arrayType.arrayOf)}[]"
  }

  override def binary(binaryType: BinaryType): String =
    if (binaryType.size.contains(1)) BIT else BYTEA

  override def fixedLengthBinary(binaryType: FixedLengthBinaryType): String =
    if (binaryType.size == 1) BIT else BYTEA

  override def integer(integerType: IntegerType): String =
    if (integerType.autoIncrement) SERIAL else INTEGER

  override def long(longType: LongType): String =
    if (longType.autoIncrement) BIGSERIAL else BIGINT

  override def string(stringType: StringType): String =
    stringType.length.filter(_ < 255) match {
      case Some(l) => s"$VARCHAR($l)"
      case None    => TEXT
    }

}
