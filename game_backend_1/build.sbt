lazy val akkaHttpVersion = "10.6.3"
lazy val akkaVersion = "2.9.3"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.twenty_nine",
      scalaVersion := "2.13.14",
      baseDirectory := file("./game-backend")
    )),
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "resources"
    name := "twenty-nine-backend",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "ch.megard" %% "akka-http-cors" % "1.2.0",

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
