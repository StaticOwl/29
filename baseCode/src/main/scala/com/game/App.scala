package com.game

import com.game.controller.GameController
import com.game.objects.{Card, MainFrameGui}

import scala.io.StdIn

/**
 * @author staticowl
 */

object App {
  
  private def foo(x : Array[String]) = x.foldLeft("")((a, b) => a + b)
  
  def main(args : Array[String]): Unit = {
    val gameController = new GameController
    gameController.setPlayers()
    val gui = new MainFrameGui(gameController.getPlayers)
    gui.startup(args)
    gameController.dealCards()
    for (elem <- gameController.getPlayers) {
      println(elem.getHand)
    }
  }

}