package com.alg.code.scala.PlaceTypeProcess

/**
 * Created by kevin on 2015/1/2.
 */

import com.util.scala.HBaseHandler
import com.alg.code.java.Point
import com.alg.code.java.Graham
import java.util.ArrayList
import java.util.Stack

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.{Delete, Get, HBaseAdmin, HTableInterface, HTablePool, Put, Result, ResultScanner, Scan}
import org.apache.hadoop.hbase.mapred.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor}

class PlaceHandler {
  val paTableName = "popular"
  val paFamilyName = "record"
  val paOutlineQual = "outline"
  val paTypeQual = "type"

  val spTableName = "sp"
  val spFamilyName = "record"
  val spQual = "desc"


  val hbHandler = new HBaseHandler()
  hbHandler.createTable(paTableName, paFamilyName)

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
/*  def analyzeRDD(){
    val confHBasePopularArea = HBaseConfiguration.create()
    confHBasePopularArea.set(TableInputFormat.INPUT_TABLE, spTableName)

    val hBaseRDD_PA = spark.newAPIHadoopRDD(confHBasePopularArea,
      classOf[TableInputFormat], classOf[org.apache.hadoop.hbase.io.Immutable.BytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    hBaseRDD_PA.map(tuple => tuple._2).map(result => (result.getRow, result.getColumn(spFamilyName.getBytes(),
      spQual.getBytes()))).map(row => {
      val key = row._1.map(_.toChar).mkString
      val all = Bytes.toString(row._2.get(0).getValue())
      key + "_" + all}).foreachPartition(x => x.foreach(_ + "_" + _))

    val numSP = hBaseRDD_PA.count
  }
*/


  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def analyzeDirect(){
    val allSP = hbHandler.getAllRecord(spTableName)
    val maxIter = 2

    val oneSet = new ArrayList[Point]()
    if(allSP != null){
      val iter = allSP.iterator()

      while(iter.hasNext()){
        val result = iter.next()
        val r = result.getRow
        val kStr = Bytes.toString(r)
        val kStrSplit = kStr.split("_")
        val userId = kStrSplit(0)
        val date = kStrSplit(1).toDouble
        val tArr = kStrSplit(2).toDouble

        val kv = result.getColumnLatest(Bytes.toBytes(spFamilyName), Bytes.toBytes(spQual))
        val vStr = Bytes.toString(kv.getValue)
        val vStrSplit = kStr.split("_")
        val lng = vStrSplit(0).toDouble
        val lat = vStrSplit(1).toDouble
        val tLea = vStrSplit(2).toDouble

        val Point = new Point(lng, lat, userId, tArr, tLea, date)
        oneSet.add(Point)

        if(oneSet.size > maxIter){
          val procResult = process(oneSet)
          storeToHBase(procResult)
          oneSet.clear()
        }

      }

      if(oneSet.size > maxIter / 10){
        val procResult = process(oneSet)
        storeToHBase(procResult)
        oneSet.clear()
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
  def process(oneSet: ArrayList[Point]): (ArrayList[Stack[Point]], ArrayList[Int]) = {
    val procArea = new PlaceAreaProcess()
    val areas = procArea.clusters(oneSet)
    val areaList = new ArrayList[Stack[Point]]
    val areaTypeList = new ArrayList[Int]

    for(i <- 0 to areas.size - 1){
      val one = areas.get(i)
      val pType = new PlaceTypeProcess(one)
      val sz = one.size
      val PointArray = new Array[Point](sz)
      for(j <- 0 to sz - 1)
        PointArray(i) = one.get(i)

      val graham = new Graham(PointArray).getHull

      areaTypeList.add(pType.category)
      areaList.add(graham)

    }
    (areaList, areaTypeList)

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
  def storeToHBase(areaList: (ArrayList[Stack[Point]], ArrayList[Int])){
    val areas = areaList._1
    val types = areaList._2
    val sz = types.size

    for(i <- 0 to sz - 1){
      val a = areas.get(i)
      val t = types.get(i).toString
      var desc = ""

      for(j <- 0 to a.size - 2){
        val g0 = a.get(j)
        desc = desc + g0.getLng.toString + "_" + g0.getLat.toString + ":"

      }

      val g1 = a.get(a.size - 1)
      desc = desc + g1.getLng.toString + "_" + g1.getLat.toString

      hbHandler.addRecords(paTableName, desc, paFamilyName, Array(paTypeQual), Array(t))

    }
  }
}
