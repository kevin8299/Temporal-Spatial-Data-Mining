package com.util.scala

/**
 * Created by kevin on 2014/12/25.
 */

import java.io.{File, Serializable}
import java.util.ArrayList

class DataConstruction(path: String) extends Serializable {
  var numFile = -1
  val user = new ArrayList[String]()
  val fileNumPerUser = new ArrayList[Int]
  val allFile = new ArrayList[String]
  val allFileAbs = new ArrayList[String]

  collectAllFiles(new File(path))

  for(i <- 0 to allFileAbs.size() - 1){
    val s = allFileAbs.get(i)
    val parts = s.split("/")  //   \\\\
    if(!user.contains(parts(3))){
      if(numFile != -1)
        fileNumPerUser.add(numFile + 1)

      numFile = 0
      user.add(parts(3))

    }
    else{
      numFile += 1
    }

    allFile.add(parts(5))
    if(i == allFileAbs.size() - 1)
      fileNumPerUser.add(numFile + 1)
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
  override def toString() = "Input Path is " + path

  /**
   * @ Description: Constructor of class Optics
   * @ Param unsortedList: Input cluster of 2D points
   * @ Param eps: Maximum distance required to get belonging points of some cluster
   * @ Param minPts: Minimum required number of points used to construct some cluster
   * @ Param debug: Debug switch specifically for distance calculation
   * @ Return: None
   * @ Throws: None
   */
  def collectAllFiles(file: File): Unit = {
    if(file.isFile()){
      //println(file.getAbsolutePath())
      allFileAbs.add(file.getAbsolutePath())
    }
    else {
      file.listFiles().foreach(collectAllFiles)
    }
  }
}
