val dottyVersion = "0.14.0-bin-20190306-9f5b3c3-NIGHTLY" // Until https://github.com/lampepfl/dotty/issues/5924 lands

lazy val root = project
  .in(file("."))
  .settings(
    name := "jobby",
    version := "2.0.0",

    scalaVersion := dottyVersion,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies ++= google,
  )

val google = Seq(
  "com.google.api-client"   % "google-api-client"          % "1.23.0",
  "com.google.oauth-client" % "google-oauth-client-jetty"  % "1.23.0",
  "com.google.apis"         % "google-api-services-sheets" % "v4-rev17-1.22.0"
)

