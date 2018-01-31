name := "jobby"

version := "1.0.0"

scalaVersion := "2.12.4"

val google = Seq(
  "com.google.api-client"   % "google-api-client"          % "1.23.0",
  "com.google.oauth-client" % "google-oauth-client-jetty"  % "1.23.0",
  "com.google.apis"         % "google-api-services-sheets" % "v4-rev17-1.22.0"
)

val testlibs = Seq(
  "org.scalatest"              %% "scalatest"                 % "3.0.5" % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.8" % "test"
)

val cats = Seq("org.typelevel" %% "cats-core" % "1.0.1")

val shapeless = Seq("com.chuusai" %% "shapeless" % "2.3.3")

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
  "-Xfatal-warnings",
  "-Ypartial-unification"
)

