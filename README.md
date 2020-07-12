# Overview

Provides the ability to create a Prototype schema in memory 
and then compare the prototype schema to an existing schema (using JDBC Database Metadata) to calculate with differences.

This project also provides Case Class wrappers to the JDBC rows returned in DatabaseMetadata ResultSets.

# Prototyping 

Create an "ideal" Schema Prototype (ProtoSchema) using the [Prototype case classes](https://github.com/scala-bones/db-prototyping/blob/master/core/src/main/scala/com/bones/mdwrap/proto/package.scala).

```$scala
  case class ProtoColumn(name: String, dataType: DataType.Value, nullable: Boolean, remark: Option[String])
  case class ProtoPrimaryKey(name: String, column: ProtoColumn)
  case class ProtoForeignKey(name: String, column: ProtoColumn, foreignReference: (ProtoTable, ProtoColumn))
  case class ProtoTable(name: String, columns: List[ProtoColumn], foreignKeys: List[ProtoForeignKey], remark: Option[String])
  case class ProtoSchema(name: String, tables: List[ProtoTable])
```

Then compare the "ideal" Schema to the database metatdata cache and find the differences between the ideal schema and the cached Database Metadata.

```$scala
val protoSchema: ProtoSchema = ???
val databaseCache: DatabaseCache = ???
val schemaDiff = Diff.findDiff(databaseCache, protoSchema)
```

TODO:
    ForeignKeys

# Database Cache
This project provides [Case Class Wrappers for JDBC Database Metadata](https://github.com/scala-bones/db-prototyping/blob/master/core/src/main/scala/com/bones/mdwrap/package.scala).

```$scala
    val createDbConnection: Connection = ???
    val cacheQuery = DatabaseQuery.everything
    val borrowConnection = new Borrow[Connection](() => createDbConnection)
    val cache = LoadDatabaseCache.load(cacheQuery, List.empty, borrowConnection) 
```

Supports:
  Column, CrossReference, PrimaryKey, Table, Schema, ImportedKeys, Function
  
TODO:
  Attribute, TablePrivilege, TypeInfo, Procedure
  
# Dependency

```libraryDependencies += "com.github.oletraveler" %% "db-prototyping" % "0.1.0-SNAPSHOT"```

# Database Support

  This project has been tested and used only with PostgreSQL.

### CI
[![Build Status](https://travis-ci.org/scala-bones/db-prototyping.svg?branch=master)](https://travis-ci.org/scala-bones/bones)    


 