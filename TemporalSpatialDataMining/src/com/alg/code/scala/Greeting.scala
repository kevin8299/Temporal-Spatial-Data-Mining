package com.alg.code.scala

/**
 * Created by kevin on 2014/12/25.
 */

import com.util.scala.DataConstruction

import scala.math.random
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import org.apache.spark._

object Greeting{
  def main(args: Array[String]){
    println("Start !")

    val conf = new SparkConf().setAppName("Spark-Geolife")
    val spark = new SparkContext(conf)
    val thetaD = 0 // 200 meters
    val thetaT = 0.02  //30 minute: 0.020833344

    val path = "/root/data1"
    val data = new DataConstruction(path)


    //val a0 = spark.textFile(data.allFileAbs.get(index), 1)
    //val a1 = a0.map(_.split("\n")).filter(_(0).length > 30)  //Array(39.984, 116.318, 0, 492, 39744.1203, 200-10-23, 02:55:12)
    //val a2 = a1.map(_(0).split(",").map(x => Array(x(0), x(1), x(3), x(4)))) //(lat, lng, alt, date)
    //val oneDay = spark.textFile(data.allFileAbs.get(index), 1).map(_.split("\n")).filter(_(0).length > 30).map(_(0).split(",").map(x => Array(x(0), x(1), x(3), x(4))))
    //oneUser.prepend(oneDay)
    //index += 1

    var prev = Array[Double]()
    val points = ListBuffer[Array[Double]]()
    val sps = ListBuffer[MeaningfulLocation]()
    val record = ListBuffer[Array[Double]]()
    def detectMeaningfulLocation(d: Array[Double]): ListBuffer[MeaningfulLocation] = {
      record.prepend(d)
      if(prev.length == 0)
        prev = d
      else{
        val dist = 0 //new MeaningfulLocation().geoDist(prev,d)
        if(dist > thetaD){
          val deltaT = scala.math.abs(prev(3) - d(3))
          if(deltaT > thetaT){
            val meanLat = points.map(_(0)).reduce(_ + _) / points.length
            val meanLng = points.map(_(1)).reduce(_ + _) / points.length
            val tArr = points(0)(4)
            val tLea = d(4)
            val sp = new MeaningfulLocation(meanLat, meanLng, tArr, tLea)
            sps.prepend(sp)
          }
          prev = d
          points.clear()
        }
        else{
          points.prepend(d)
        }
      }
      sps
    }
  }
}