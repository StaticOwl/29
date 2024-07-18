package com.twenty_nine.game.exceptions

import com.twenty_nine.game.objects.Card


/**
 * @author staticowl
 */

class NotEnoughPlayersException(message: String) extends Exception(message)
class UnsupportedTypeException(message: String) extends Exception(message)
class BadDealException(message: String, deck : Seq[Card]) extends Exception(message){
  override def getMessage: String = super.getMessage + deck.toString()
}