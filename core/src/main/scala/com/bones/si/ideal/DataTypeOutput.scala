package com.bones.si.ideal

/**
 * Trait which is extended to produce an output.  Eg SQL text output.
 * @tparam A The output type.
 */
trait DataTypeOutput[A] {

  /** Produce output for a specific column.  For instance 'id int8 not null'. */
  def protoColumnOutput(protoColumn: IdealColumn): A

  /** Responsible for delegating to the appropriate method based on the data type.*/
  def dataTypeOutput(dataType: IdealDataType): A = {
    dataType match {
      case a: ArrayType => array(a)
      case b: BinaryType => binary(b)
      case BooleanType => boolean
      case f: FixedLengthBinaryType => fixedLengthBinary(f)
      case f: FixedLengthCharacterType => fixedLengthCharacter(f)
      case DateType => date
      case DoubleType => double
      case IntervalType => interval
      case i: IntegerType => integer(i)
      case l: LongType => long(l)
      case n: NumericType => numeric(n)
      case RealType => real
      case SmallIntType => smallInt
      case t: TimeType => time(t)
      case t: TimestampType => timestamp(t)
      case s: StringType => string(s)
    }
  }

  def array(arrayType: ArrayType): A
  def binary(binaryType: BinaryType): A
  def boolean: A
  def fixedLengthBinary(binaryType: FixedLengthBinaryType): A
  def fixedLengthCharacter(fixedLengthCharacterType: FixedLengthCharacterType): A
  def date: A
  def double: A
  def integer(integerType: IntegerType): A
  def interval: A
  def long(longType: LongType): A
  def numeric(numericType: NumericType): A
  def real: A
  def time(timeType: TimeType): A
  def timestamp(timestampType: TimestampType): A
  def smallInt: A
  def string(stringType: StringType): A

}
