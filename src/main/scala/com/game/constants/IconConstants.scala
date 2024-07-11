package com.game.constants

import com.game.objects._

import java.util
import scala.swing.Component

object IconConstants {

  private val icons: Map[Any, String] = Map[Any, String](
    Hearts -> "some",
    Jack -> "some"
  )

  def getIcons(card: Card): Unit = {
    val suit = card.suit
    val rank = card.rank

  }

}
