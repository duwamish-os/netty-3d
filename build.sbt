import sbt.Keys.mainClass

name := "netty-3d"

version := "0.1"

scalaVersion := "2.12.5"

lazy val root = project.aggregate(server, client).dependsOn(server, client)

lazy val server = project
  .settings(
    name := "server",
    libraryDependencies ++= Seq(dependencies.netty),
    mainClass in Compile := Some("com.netty.threed.Server"))
  .dependsOn(client)

lazy val dependencies =
  new {
    val netty = "io.netty" % "netty-all" % "4.1.23.Final"
  }


lazy val client = project
  .settings(
    name := "client"
  )


//mainClass in(Compile, run) := (mainClass in run in server).value
run in Compile <<= (run in Compile in server)