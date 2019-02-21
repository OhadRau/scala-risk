name := "backend"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += ws

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.21" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.21" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.1.3" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.1.7" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.21" % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
