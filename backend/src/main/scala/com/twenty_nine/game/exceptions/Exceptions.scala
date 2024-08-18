package com.twenty_nine.game.exceptions

import com.twenty_nine.game.objects.Card


/**
 * @author staticowl
 */

case class NotEnoughPlayersException(message: String) extends Exception(message)
case class UnsupportedTypeException(message: String) extends Exception(message)
case class BadDealException(message: String, deck : Seq[Card]) extends Exception(message){
  override def getMessage: String = super.getMessage + deck.toString()
}
case class CardNotFoundException(message: String) extends Exception(message)
case class NoPointAssignedException(message: String) extends Exception(message)