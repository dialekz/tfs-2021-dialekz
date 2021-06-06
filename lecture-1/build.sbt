name := "lecture-1"
version := "0.1"
scalaVersion := "2.13.4"

resolvers += "Tinkoff Scala resolver" at "https://gitlab.com/api/v4/projects/24262845/packages/maven"

libraryDependencies ++= Seq(
  "ru.tinkoff" %% "scala-course-tom" % "0.2",
  "ru.tinkoff" %% "scala-course-john" % "0.2" exclude("ru.tinkoff", "scala-course-clerk_2.13")
)
