name := """MERA"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)

scalaVersion := "2.11.7"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.play" %% "play-mailer" % "3.0.1" ,
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.5-P24",
  "junit" % "junit" % "4.12" % "test",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
  "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.4",
  "org.apache.httpcomponents" % "httpclient" % "4.3.4",
  "org.apache.spark" % "spark-core_2.11" % "1.2.0",
  "com.google.guava" % "guava" % "18.0",
  "org.apache.httpcomponents" % "httpcore" % "4.3.2"

)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
