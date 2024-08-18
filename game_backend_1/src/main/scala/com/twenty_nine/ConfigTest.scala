package com.twenty_nine

import com.typesafe.config.ConfigFactory

object ConfigTest extends App {
  val config = ConfigFactory.load()
  println("Loaded config: " + config.getConfig("akka-http-server-cors").toString)
}

