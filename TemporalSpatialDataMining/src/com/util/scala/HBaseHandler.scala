package com.util.scala

/**
 * Created by kevin on 2014/12/25.
 * This class is a set of implementation functions for HBase read and write
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
   * @ Description: Create table in HBase
   * @ Param tableName: Input table name 
   * @ Param family: Input family name 
   * @ Return: None
   * @ Throws: HBaseException
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
   * @ Description: Delete one table in HBase
   * @ Param tableName: Input table name 
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
   * @ Description: Get the table's  HTableInterface
   * @ Param tableName: Input table name 
   * @ Return: HTableInterface for handling
   * @ Throws: HBaseException
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
   * @ Description: Add one dataset into one table in HBase
   * @ Param tableName: Input table name 
   * @ Param rowKey: Input row key
   * @ Param family: Input family name 
   * @ Param qualifier: Input qualifier name 
   * @ Param value: Input value
   * @ Return: None
   * @ Throws: HBaseException
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
   * @ Description: Delete one record in HBase
   * @ Param tableName: Input table name 
   * @ Param rowKey: Input row key 
   * @ Return: None
   * @ Throws: HBaseException
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
   * @ Description: Delete one record in HBase
   * @ Param table: Input HTableInterface
   * @ Param rowKey: Input row key 
   * @ Return: None
   * @ Throws: HBaseException
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
   * @ Description: Delete one table in HBase
   * @ Param tableName: Input table name 
   * @ Return: None
   * @ Throws: HBaseException
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
   * @ Description: Close one table in HBase
   * @ Param table: Input HTableInterface
   * @ Return: None
   * @ Throws: HBaseException
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
   * @ Description: Get one record from HBase
   * @ Param table: Input HTableInterface
   * @ Param rowKey: Input row key 
   * @ Return: The Result class
   * @ Throws: HBaseException
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
   * @ Description: Get all records of one table from HBase
   * @ Param table: Input HTableInterface
   * @ Return: The ResultScanner class
   * @ Throws: HBaseException
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
