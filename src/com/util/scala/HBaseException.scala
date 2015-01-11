package com.util.scala

/**
 * Created by kevin on 2014/12/25.
 */

 
 /**
  This class is just a simple exception for HBase
 */
class HBaseException(val id: Int = 400,
                      val msg: String = "HBaseException") extends Exception{

  /**
   * @ Description: toString
   * @ Param: None
   * @ Return: A string
   * @ Throws: None
   */
  override def toString(): String = {
    msg
  }
}
