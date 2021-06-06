
ThisBuild / scalaVersion := "2.13.4"
ThisBuild / libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "3.2.2",
  "org.scalatest" %% "scalatest" % "3.2.2" % Test
)

lazy val lecture = project
lazy val homework = project