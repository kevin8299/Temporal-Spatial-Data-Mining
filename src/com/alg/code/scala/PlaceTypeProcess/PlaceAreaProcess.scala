package com.alg.code.scala.PlaceTypeProcess

import java.util.ArrayList

import com.alg.code.java.{Point, Optics}
import com.demo.debug.java.Clastering2PolygonDemo

/**
 * Created by kevin on 2015/1/2.
 */

class PlaceAreaProcess{
  val eps = 20
  val minPts = 10

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def clusters(d: ArrayList[Point]): ArrayList[ArrayList[Point]] = {
    new Optics(d, eps, minPts, true).clusters()

  }

}


object PlaceAreaProcess{

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def main(args: Array[String]): Unit = {
    val testR = testClusters(Clastering2PolygonDemo.initTestData())
    for(i <- 0 to testR.size - 1){
      val set = testR.get(i)
      val sz = set.size

      if(sz != 0){
        var sumX = 0.0
        var sumY = 0.0
        for(j <- 0 to sz - 1){
          sumX = sumX + set.get(j).getLng
          sumY = sumY + set.get(j).getLat
        }
        println("One cluster is " + sumX / sz + " " + sumY / sz + "size " + sz)

      }

    }

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
  def testClusters(d: ArrayList[Point]): ArrayList[ArrayList[Point]] = {
    new PlaceAreaProcess().clusters(d)
  }

}
