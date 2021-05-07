import sbt._
import Keys._

name := "functional-ws-pres"
organization := "com.softwaremill"
scalaVersion := "2.13.5"

val tapirVersion = "0.18.0-M9"
val circeVersion = "0.13.0"
val sttpVersion = "3.3.0"
val http4sVersion = "0.21.22"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % sttpVersion,
  "com.softwaremill.sttp.client3" %% "circe" % sttpVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-docs" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-circe-yaml" % tapirVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"
)

commonSmlBuildSettings
