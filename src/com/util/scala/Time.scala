package com.util.scala

/**
 * Created by kevin on 2015/1/2.
 */

class Time(val tIn: Double){
  val t = tIn - tIn.floor

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def getHour(): Int = {
    val h = (t - t.floor) * 24
    h.floor.toInt
  }

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def getMinute(): Int = {
    val m = (t * 24 - getHour().floor) * 60
    m.floor.toInt
  }

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def getSecond(): Int = {
    val m = (t * 24 - getHour()) * 60
    val s = (m - getMinute()) * 60
    s.ceil.toInt
  }

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
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
