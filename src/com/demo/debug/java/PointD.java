package com.demo.debug.java;

/**
 * Created by kevin on 2015/1/4.
 */
public class PointD {
    int x, y;
    long t;

    /**
     * @ Description: Constructor of class Optics
     * @ Param unsortedList: Input cluster of 2D points
     * @ Param eps: Maximum distance required to get belonging points of some cluster
     * @ Param minPts: Minimum required number of points used to construct some cluster
     * @ Param debug: Debug switch specifically for distance calculation
     * @ Return: None
     * @ Throws: None
     */
    public PointD(){}

    /**
     * @ Description: Constructor of class Optics
     * @ Param unsortedList: Input cluster of 2D points
     * @ Param eps: Maximum distance required to get belonging points of some cluster
     * @ Param minPts: Minimum required number of points used to construct some cluster
     * @ Param debug: Debug switch specifically for distance calculation
     * @ Return: None
     * @ Throws: None
     */
    public PointD(int xIn, int yIn, long tIn){
        x = xIn;
        y = yIn;
        t = tIn;
    }
}
