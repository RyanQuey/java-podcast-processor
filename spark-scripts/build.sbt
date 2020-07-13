// NOTE sets scalaVersion for all subprojects too
ThisBuild / scalaVersion := "2.11.12"
name := "Spark Scripts for Podcast Analysis Tool"
version := "0.3.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.6" % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.6" % "provided"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.6" % "provided"

