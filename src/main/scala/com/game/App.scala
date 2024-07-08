package com.game

/**
 * @author ${user.name}
 */
object App {
  
  private def foo(x : Array[String]) = x.foldLeft("")((a, b) => a + b)
  
  def main(args : Array[String]): Unit = {
    println( "Hello World!" )
    println("concat arguments = " + foo(args))
  }

}
