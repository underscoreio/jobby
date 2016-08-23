name := "jobby"

version := "1.0.0"

scalaVersion := "2.11.8"

val google = Seq(
  "com.google.api-client"   % "google-api-client"          % "1.22.0",
  "com.google.oauth-client" % "google-oauth-client-jetty"  % "1.22.0",
  "com.google.apis"         % "google-api-services-sheets" % "v4-rev17-1.22.0"
)

val scalatest = Seq("org.scalatest" %% "scalatest" % "3.0.0" % "test")

val cats = Seq("org.typelevel" %% "cats" % "0.6.1")

val shapeless = Seq("com.chuusai" %% "shapeless" % "2.3.2")

libraryDependencies ++= google ++ scalatest ++ cats ++ shapeless

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Xlint",
  "-Xfatal-warnings"
)

