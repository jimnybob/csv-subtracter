ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "csv-subtracter"
  )

libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test"