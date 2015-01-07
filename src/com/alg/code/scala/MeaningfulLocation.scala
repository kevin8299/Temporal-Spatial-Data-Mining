package com.alg.code.scala

/**
 * Created by kevin on 2014/12/25.
 */

import com.util.scala.HBaseHandler
import java.io.Serializable

class MeaningfulLocation(val lat: Double = 0.0,
                          val lng: Double = 0.0,
                          val alt: Double = 0.0,
                          val tArr: Double = 0.0,
                          val tLea: Double = 0.0,
                          val info: String = "",
                          val num: Int = 0) extends Serializable {


  val thetaD = 50    //200
  val thetaT = 1000  //0.02   ---   30 minutes = 0.0208

  val TABLE_STAYPOINT = "sp"
  val FAMILY_STAYPOINT = "record"
  val QUALIFIER_STAYPOINT = "desc"

  val hbHandler = new HBaseHandler()
  hbHandler.createTable(TABLE_STAYPOINT, FAMILY_STAYPOINT)

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def MeaningfulLocation(){}

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def detect(x: String, y: String): String = {
    var xLng: Double = 0.0
    var xLat: Double = 0.0
    var xT: Double = 0.0

    val xLen = x.split(":").length
    if(xLen == 1){
      val dx = x.split("_")
      xLng = dx(0 + 2).toDouble
      xLat = dx(1 + 2).toDouble
      xT = dx(3 + 2).toDouble
    }
    else{
      val sum = x.split(":").map(x =>
        (x.split("_")(0 + 2).toDouble,
          x.split("_")(1 + 2).toDouble,
            x.split("_")(3 + 2).toDouble,
              0.0)).reduce((x, y) =>
              (x._1 + y._1,
                x._2 + y._2,
                if(x._3 > y._3) y._3 else x._3,
                if(x._3 > y._3) x._3 else y._3))

                xLng = sum._1 / x.split(":").length
                xLat = sum._2 / x.split(":").length
                xT = sum._3
    }

    val dy = y.split("_")
    val yLng = dy(0 + 2).toDouble
    val yLat = dy(1 + 2).toDouble
    val deltaD = geoDist(xLng, xLat, yLng, yLat)
    if(deltaD > thetaD){
      val yT = dy(3 + 2).toDouble
      val deltaT = scala.math.abs(xT - yT)
      if(deltaT > thetaT)
        spCalcStore(x)
      y
    }
    else{
      val out = x + ":" + y
      out
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
  def spCalcStore(x: String): Unit = {
    var sumLng = 0.0
    var sumLat = 0.0
    var tArr = Double.MaxValue
    var tLea = 0.0
    var meanLng = 0.0
    var meanLat = 0.0

    val xSplit = x.split(":")
    var key = xSplit(0).split("_")(0) + "_" + xSplit(0).split("_")(1)

    //calculation
    if(xSplit.length > 1){
      for(i <- 0 to xSplit.size - 1){
        val dx = xSplit(i).split("_")
        val xLng = dx(0 + 2).toDouble
        val xLat = dx(1 + 2).toDouble
        val xT = dx(3 + 2).toDouble

        sumLng += xLng
        sumLat += xLat

        if(tArr > xT) tArr = xT
        if(tLea < xT) tLea = xT

      }
      meanLng = sumLng / xSplit.size
      meanLat = sumLat / xSplit.size

    }
    else{
      tArr = xSplit(0).split("_")(3 + 2).toDouble
      tLea = tArr
      meanLng = xSplit(0).split("_")(0 + 2).toDouble
      meanLat = xSplit(0).split("_")(1 + 2).toDouble
    }

    //store to HBase
    /*
    Table Design Policy
    key: userId_date_enterTime
    value: lng_lat_leaveTime_info_times
    */

    key = key + "_" + tArr
    val value = meanLng.toString + "_" + meanLat.toString + "_" +
      tLea.toString + "_" + "null" + "_" + "1"

    hbHandler.addRecords(TABLE_STAYPOINT, key, FAMILY_STAYPOINT, Array(QUALIFIER_STAYPOINT), Array(value))
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
  def detectStayPoint(key: String, data: String): Unit = {
    val dataSplit = data.split(":")
    dataSplit.map(x => key + "_" +x).reduce(detect)
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
  def geoDist(lng1: Double, lat1: Double, lng2: Double, lat2: Double, debug: Boolean = true): Double = {
    if (debug) {
      val disX: Double = lng1 - lng2
      val disY: Double = lat1 - lat2

      Math.sqrt(disX * disX + disY * disY)
    }
    else {
      val EARTH_RADIUS: Double = 6378137.0
      val dLat: Double = Math.toRadians(lat2 - lat1)
      val dLng: Double = Math.toRadians(lng2 - lng1)
      val a: Double = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2)
      val c: Double = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
      val dist: Double = EARTH_RADIUS * c
      dist
    }
  }

}

object MeaningfulLocation{

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def detect(key: String, data: String): Unit = {
    new MeaningfulLocation().detectStayPoint(key, data)
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
  def main(args: Array[String]){
    val rawTestData = ""
    detect("testKey", rawTestData)

  }
}