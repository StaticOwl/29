package com.game.rules

import com.game.objects.{Card, Player}

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

/**
 * @author staticowl
 */

trait Rules {

  def trump(): Unit

  def getCall(): Int
}
