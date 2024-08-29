ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.1"

lazy val root = (project in file("."))
  .settings(
    name := "backend"
  )

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-hive
libraryDependencies += "org.apache.spark" %% "spark-hive" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.3.1"

libraryDependencies += "com.typesafe" % "config" % "1.4.0"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.1"

libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "12.0"

libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.13.1"

//REST

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.8.0-M4",
  "com.typesafe.akka" %% "akka-http" % "10.5.0-M1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0-M1",
  // https://mvnrepository.com/artifact/ch.megard/akka-http-cors
  "ch.megard" %% "akka-http-cors" % "1.2.0")