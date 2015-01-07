package com.util.scala

/**
 * Created by kevin on 2014/12/25.
 */

import java.io.{IOException, Serializable}
import java.util.ArrayList

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.{Delete, Get, HBaseAdmin, HTableInterface, HTablePool, Put, Result, ResultScanner, Scan}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor}

class HBaseHandler extends Serializable {
  val TablePoolNum = 10
  val cfg = new Configuration()
  cfg.set("hbase.zookeeper.quorum", "kevin_computer")
  cfg.set("hbase.zookeeper.property.clientPort", "2181")
  val c = new HBaseConfiguration(cfg)
  val hTablePool = new HTablePool(c, TablePoolNum)

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def createTable(tableName: String, family: String = "location"): Unit = {
    val hAdmin = new HBaseAdmin(c)
    if (hAdmin.tableExists(tableName)) {
      println(tableName + "has exist !")
      throw new HBaseException(400, "createTable")
    }
    else {
      val tableDesc = new HTableDescriptor(tableName)
      tableDesc.addFamily(new HColumnDescriptor(family))
      hAdmin.createTable(tableDesc)
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
  def deleteTable(tableName: String): Unit = {
    val hAdmin = new HBaseAdmin(c)
    if (hAdmin.tableExists(tableName)) {
      hAdmin.disableTable(tableName)
      hAdmin.deleteTable(tableName)
    }
    else {
      println(tableName + "is NOT existed !")
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
  def getHTable(tableName: String): HTableInterface = {
    var table: HTableInterface = null

    if (hTablePool == null) {
      println("HBase is NOT initialized well !")
      throw new HBaseException(400, "getTable")
    }
    else {
      table = hTablePool.getTable(tableName)
    }

    table
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
  def addRecords(tableName: String, rowKey: String, family: String = "location",
                 qualifier: Array[String], value: Array[String]) {
    val table = getHTable(tableName)
    try {
      val put = new Put(Bytes.toBytes(rowKey))
      for (i <- 0 to qualifier.length - 1)
        put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier(i)), Bytes.toBytes(value(i)))
      table.put(put)
    } catch {
      case e: IOException => throw new HBaseException(400, "addRecords")

    } finally {
      closeTable(table)
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
  def delRecord(tableName: String, rowKey: String) {
    val table = getHTable(tableName)
    try {
      val list = new ArrayList[Delete]()
      val del = new Delete(rowKey.getBytes())
      list.add(del)
      table.delete(list)
    } catch {
      case e: IOException => throw new HBaseException(400, "delRecord")

    } finally {
      closeTable(table)
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
  def delRecord(table: HTableInterface, rowKey: String) {
    try {
      val list = new ArrayList[Delete]()
      val del = new Delete(rowKey.getBytes())
      list.add(del)
      table.delete(list)
    } catch {
      case e: IOException => throw new HBaseException(400, "delRecord")

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
  def delAllRecord(tableName: String) {
    val allUserBase = getAllRecord(tableName)
    if (allUserBase != null) {
      val rows = new ArrayList[String]()
      val iter = allUserBase.iterator()
      while (iter.hasNext()) {
        val result = iter.next()
        val row = new String(result.getRow())
        rows.add(row)
      }

      val table = getHTable(tableName)
      try {
        for (i <- 0 to rows.size() - 1) {
          val row = rows.get(i)
          delRecord(table, row)
        }
      } catch {
        case e: IOException => throw new HBaseException(400, "delRecord")

      } finally {
        closeTable(table)
        allUserBase.close()
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
  def closeTable(table: HTableInterface) {
    if (table != null) {
      try {
        table.close()
      } catch {
        case e: IOException => throw new HBaseException(400, "closeTable")
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
  def getOneRecord(tableName: String, rowKey: String): Result = {
    val table = getHTable(tableName)
    val get = new Get(rowKey.getBytes())
    var result: Result = null

    try {
      result = table.get(get)
    } catch {
      case e: IOException => throw new HBaseException(400, "getOneRecord")
    }

    result
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
  def getAllRecord(tableName: String): ResultScanner = {
    val table = getHTable(tableName)
    var scanResults: ResultScanner = null

    try {
      val scan = new Scan()
      scanResults = table.getScanner(scan)
    } catch {
      case e: IOException => throw new HBaseException(400, "getAllRecord")
    } finally {
      closeTable(table)
    }

    scanResults
  }
}
