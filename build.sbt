name := "jobby"

version := "1.0.0"

scalaVersion := "2.12.0"

val google = Seq(
  "com.google.api-client"   % "google-api-client"          % "1.22.0",
  "com.google.oauth-client" % "google-oauth-client-jetty"  % "1.22.0",
  "com.google.apis"         % "google-api-services-sheets" % "v4-rev17-1.22.0"
)

val testlibs = Seq(
  "org.scalatest"              %% "scalatest"                 % "3.0.0" % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.3" % "test"
)

val cats = Seq("org.typelevel" %% "cats" % "0.8.1")

val shapeless = Seq("com.chuusai" %% "shapeless" % "2.3.2")

libraryDependencies ++= google ++ testlibs ++ cats ++ shapeless

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Xlint",
  "-Xfatal-warnings"
)

