package com.bones.mdwrap.proto

trait DataTypeOutput[A] {

  def protoColumnOutput(protoColumn: ProtoColumn): A

  /** Responsible for delegating to the appropriate method */
  def dataTypeOutput(dataType: ProtoDataType): A = {
    dataType match {
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
