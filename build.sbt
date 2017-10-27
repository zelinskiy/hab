val ScalatraVersion = "2.5.3"

organization := "com.example"

name := "hab"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.3"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

  "org.squeryl" %% "squeryl" % "0.9.5-7",
  "mysql" % "mysql-connector-java" % "5.1.12",
  "c3p0" % "c3p0" % "0.9.1.2",

  "org.scalatra" %% "scalatra-auth" % ScalatraVersion
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
