package com.alg.code.scala.PlaceTypeProcess

/**
 * Created by kevin on 2015/1/2.
 * This class is for extracting the location type, which now is just separated into 4 types, which is listed below
 */

import java.util.ArrayList

import com.alg.code.java.{Optics,Point}

import com.util.scala.Time

import scala.util.Random

class PlaceTypeProcess(val area: ArrayList[Point]){
  val eps = 0.02
  val minPts = 5
  val category = typeRecognizer()

  /*
    Place type is mainly based on active time span
    0. 16:00 -- 23:00 enter && 06:00 -- 10:00 leave   "Home Area"
    1. 07:00 -- 10:00 enter && 16:00 -- 20:00 leave   "Work Area"
    2. 11:00 -- 13:00 && 17:00 -- 22:00               "Food Area"
    3. 23:00 -- 05:00                                 "Night Area"
  */

  /**
   * @ Description: Recognize the type of meaningful location mainly based on activity time span
   * @ Param: None
   * @ Return: Area type
   * @ Throws: None
   */
  def typeRecognizer(): Int = {
    val span = timeSpan()
    var homeAreaVote = 0
    var workAreaVote = 0
    var foodAreaVote = 0
    var nightAreaVote = 0

    //arrive
    val arrMeans = span._1._1
    for(i <- 0 to arrMeans.size - 1){
      val t = arrMeans.get(i)
      val tHour = new Time(t).getHour()
      if(tHour >= 16 && tHour <= 23)
        homeAreaVote = homeAreaVote + 1
      else if(tHour >= 7 && tHour <= 10)
        workAreaVote = workAreaVote + 1
      else if((tHour >= 11 && tHour <= 13) || (tHour >= 17 && tHour <= 22))
        foodAreaVote = foodAreaVote + 1
      else if(tHour >= 23)
        nightAreaVote = nightAreaVote + 1

    }

    //leave
    val leaMeans = span._2._1
    for(i <- 0 to leaMeans.size - 1){
      val t = leaMeans.get(i)
      val tHour = new Time(t).getHour()
      if(tHour >= 6 && tHour <= 10)
        homeAreaVote = homeAreaVote + 1
      else if(tHour >= 16 && tHour <= 20)
        workAreaVote = workAreaVote + 1
      else if((tHour >= 1 && tHour <= 13) || (tHour >= 17 && tHour <= 22))
        foodAreaVote = foodAreaVote + 1
      else if(tHour >= 0 && tHour <= 5)
        nightAreaVote = nightAreaVote + 1

    }

    var result = 0
    var inter = homeAreaVote
    if(workAreaVote > inter){
      result = 1
      inter = workAreaVote
    }

    if(foodAreaVote > inter){
      result = 2
      inter = foodAreaVote
    }

    if(nightAreaVote > inter){
      result = 3
    }

    result
  }

  /**
   * @ Description: Extract the activity time span of the meaningful location 
   * @ Param: None
   * @ Return: Arrive and leave time span
   * @ Throws: None
   */
  def timeSpan(): ((ArrayList[Double], ArrayList[Double]), (ArrayList[Double], ArrayList[Double])) = {
    val tArr = new ArrayList[Point]
    val tLea = new ArrayList[Point]

    for(i <- 0 to area.size - 1){
      val ta = area.get(i).getLng
      val tl = area.get(i).getLat
      val g1 = new Point(ta, 0)
      tArr.add(g1)
      val g2 = new Point(tl, 0)
      tLea.add(g2)

    }

    //Enter time extraction
    val tArrSpan = timeSpanCalc(tArr)

    //Leave time extraction
    val tLeaSpan = timeSpanCalc(tLea)

    (tArrSpan, tLeaSpan)

  }

  /**
   * @ Description: Constructor of class Optics
   * @ Param in: All points in meaningful area 
   * @ Return: Mean value and variance value of the time span
   * @ Throws: None
   */
  def timeSpanCalc(in: ArrayList[Point]): (ArrayList[Double], ArrayList[Double]) = {
    val oneSetResultMean = new ArrayList[Double]()
    val oneSetResultVar = new ArrayList[Double]()

    val tCluster = new Optics(in, eps, minPts, true).opticsCalc()

    val oneSet = new ArrayList[Double]()
    for(i <- 0 to tCluster.size - 1){
      val d = tCluster.get(i)
      if(d.getReachDist == -1){
        val sz = oneSet.size()
        if(sz > 0){
          val r = calc(oneSet)
          oneSetResultMean.add(r._1)
          oneSetResultVar.add(r._2)
          oneSet.clear()
        }
      }
      else{
        oneSet.add(d.getLng)
      }
    }

    if(oneSet.size > 0){
      val r = calc(oneSet)
      oneSetResultMean.add(r._1)
      oneSetResultVar.add(r._2)
      oneSet.clear()
    }

    (oneSetResultMean, oneSetResultVar)
  }

  /**
   * @ Description: Calculate mean value and variance value 
   * @ Param oneSet: Input datasets
   * @ Return: Mean value and variance value 
   * @ Throws: None
   */
  def calc(oneSet: ArrayList[Double]): (Double, Double) = {
    val sz = oneSet.size()
    var sum = 0.0
    for(i <- 0 to sz - 1)
      sum = sum + oneSet.get(i)
    val mean = sum / sz

    sum = 0.0
    for(j <- 0 to sz - 1)
      sum = sum + Math.pow(oneSet.get(j) - mean , 2)
    val variance = Math.sqrt(sum /sz)

    (mean, variance)
  }
}

object PlaceTypeProcess{

  /**
   * @ Description: For unit test of calculating type info of meaningful area
   * @ Param args: Input options
   * @ Return: None
   * @ Throws: None
   */
  def main(args: Array[String]){
    /*
      Home Test
      enter: 19:00  ---- 0.79166666
      leave: 9:00   ---- 0.375
    */

    val homeEnter = 0.79166666
    val homeLeave = 0.375
    val area = new ArrayList[Point]
    
    for(i <- 0 to 20){
      val tA = homeEnter + Random.nextDouble() / 24
      val tL = homeLeave + Random.nextDouble() / 24
      val p = new Point(tA, tL)
      area.add(p)
    }

    val ptProc = new PlaceTypeProcess(area)
    println("Test Result : " + ptProc.category)

  }

}
