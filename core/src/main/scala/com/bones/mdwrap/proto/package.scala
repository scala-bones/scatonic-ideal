package com.bones.mdwrap

package object proto {

  case class ProtoColumn(name: String, dataType: DataType.Value, nullable: Boolean, remark: Option[String])
  case class ProtoPrimaryKey(name: String, column: ProtoColumn)
  case class ProtoForeignKey(name: String, column: ProtoColumn, foreignReference: (ProtoTable, ProtoColumn))
  case class ProtoTable(name: String, columns: List[ProtoColumn], foreignKeys: List[ProtoForeignKey], remark: Option[String])
  case class ProtoSchema(name: String, tables: List[ProtoTable])

}
