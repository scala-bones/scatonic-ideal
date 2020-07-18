package com.bones.si.ideal

object PostgresSqlOutput {
  val uppercase = PostgresSqlOutput(_.toUpperCase)
  val lowercase = PostgresSqlOutput(_.toLowerCase)
}
case class PostgresSqlOutput protected (toCaseFunction: String => String)
    extends SchemaOutput(toCaseFunction, DataTypePostgresSqlOutput(toCaseFunction))
