name := "jobby"

version := "1.0.0"

scalaVersion := "2.13.1"

resolvers += ("google" at "https://dl.google.com/dl/android/maven2/")

val google = Seq(
  "com.google.api-client"   % "google-api-client"          % "1.30.8",
  "com.google.oauth-client" % "google-oauth-client-jetty"  % "1.30.5",
  "com.google.apis"         % "google-api-services-sheets" % "v4-rev581-1.25.0"
)

val testlibs = Seq(
  "org.scalatest"              %% "scalatest"                 % "3.0.8" % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.3" % "test"
)

val cats = Seq("org.typelevel" %% "cats-core" % "2.1.0")

val shapeless = Seq("com.chuusai" %% "shapeless" % "2.3.3")

val yaml = Seq("org.yaml" % "snakeyaml" % "1.25")

libraryDependencies ++= google ++ testlibs ++ cats ++ shapeless ++ yaml

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
)

