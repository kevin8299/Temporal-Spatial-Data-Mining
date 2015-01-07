package com.util.scala

/**
 * Created by kevin on 2014/12/25.
 */

class HBaseException(val id: Int = 400,
                      val msg: String = "HBaseException") extends Exception{

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  override def toString(): String = {
    msg
  }
}
