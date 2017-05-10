name := "Kafka - Avro"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.7.7",
  // Logback with slf4j facade
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "ch.qos.logback" % "logback-core" % "1.0.13",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.5",
  "org.apache.kafka" % "kafka_2.11" % "0.10.0.0"
    exclude("javax.jms", "jms")
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri")
    exclude("log4j", "log4j")
    exclude("org.slf4j","slf4j-log4j12")

)