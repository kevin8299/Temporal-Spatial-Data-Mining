package com.util.scala

/**
 * Created by kevin on 2015/1/2.
 * This class is for tranferring the double time in Geolife dataset to Hour/Minute/Second form
 */
class Time(val tIn: Double){
  val t = tIn - tIn.floor

  /**
   * @ Description: Get hour from input data
   * @ Return: The hour in 24hours format
   * @ Throws: None
   */
  def getHour(): Int = {
    val h = (t - t.floor) * 24
    h.floor.toInt
  }

  /**
   * @ Description: Get minute from input data
   * @ Return: The minute 
   * @ Throws: None
   */
  def getMinute(): Int = {
    val m = (t * 24 - getHour().floor) * 60
    m.floor.toInt
  }

  /**
   * @ Description: Get second from input data
   * @ Return: The second
   * @ Throws: None
   */
  def getSecond(): Int = {
    val m = (t * 24 - getHour()) * 60
    val s = (m - getMinute()) * 60
    s.ceil.toInt
  }

  /**
   * @ Description: Get the numbers in a week from the input data
   * @ Return: The numbers in a week
   * @ Throws: None
   */
  def getNum(): Int = {
    val n = (tIn.floor -1) % 7
    if(n == 0)
      7
    else
      n.toInt
  }

}
