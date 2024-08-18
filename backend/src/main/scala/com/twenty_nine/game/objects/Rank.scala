package com.twenty_nine.game.objects

sealed trait Rank {
  def points: Option[Int] = None
}

case object Jack extends Rank {
  override val points: Option[Int] = Some(3)
}

case object Nine extends Rank {
  override val points: Option[Int] = Some(2)
}

case object Ace extends Rank {
  override val points: Option[Int] = Some(1)
}

case object Ten extends Rank {
  override val points: Option[Int] = Some(1)
}

case object King extends Rank {
  override val points: Option[Int] = Some(0)
}

case object Queen extends Rank {
  override val points: Option[Int] = Some(0)
}

case object Eight extends Rank {
  override val points: Option[Int] = Some(0)
}

case object Seven extends Rank {
  override val points:Option[Int] = Some(0)
}

/*Trump Card Ranks*/
case object Two extends Rank
case object Three extends Rank
case object Four extends Rank
case object Five extends Rank
case object Six extends Rank