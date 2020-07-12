lazy val commonSettings = Seq(
  organization := "io.github.scala-bones",
  scalaVersion := "2.13.2",
  version := "0.1.0-SNAPSHOT",
  homepage := Some(url("https://github.com/scala-bones/db-prototyping")),
  startYear := Some(2020),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  pomExtra := {
    <scm>
      <url>git://github.com/scala-bones/db-prototyping.git</url>
      <connection>scm:git://github.com/scala-bones/db-prototyping.git</connection>
    </scm>
    <developers>
      <developer>
        <id>oletraveler</id>
        <name>Travis Stevens</name>
        <url>https://github.com/oletraveler</url>
      </developer>
    </developers>
  },
  resolvers += Resolver.sonatypeRepo("releases"),
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    name := "db-prototyping",
    libraryDependencies ++= Seq(
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % Test,
      "org.scalatest" %% "scalatest-mustmatchers" % "3.2.0" % Test
    ),
    description := "Core classes use for Native JDBC and Doobie Implementations"
  )

lazy val jdbc = (project in file("jdbc"))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    commonSettings,
    parallelExecution in IntegrationTest := false,
    name := "jdbc-db-md-wrapper",
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.2.14" % "test,it",
      "org.scalatest" %% "scalatest" % "3.2.0" % "test,it",
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % "test,it"
    )
  )
  .dependsOn(core)

lazy val doobieVersion = "0.9.0"
lazy val doobie = (project in file("doobie"))
  .settings(
    commonSettings,
    name := "jdbc-db-md-wrapper",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core" % doobieVersion,      
      "org.scalatest" %% "scalatest" % "3.2.0" % Test,
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % Test
    )
  )
  .dependsOn(core)



