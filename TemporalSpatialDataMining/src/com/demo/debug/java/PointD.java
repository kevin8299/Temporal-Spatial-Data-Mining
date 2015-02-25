package com.demo.debug.java;

/**
 * Created by kevin on 2015/1/4.
 * This class is a 2D point class used for Demos
 */
public class PointD {
    int x, y;
    long t;

    /**
     * @ Description: Constructor of class PointD
     * @ Param: None
     * @ Return: None
     * @ Throws: None
     */
    public PointD(){}

    /**
     * @ Description: Constructor of class PointD
     * @ Param xIn: x coordinate
	 * @ Param yIn: y coordinate
	 * @ Param tIn: time dimension
     * @ Return: None
     * @ Throws: None
     */
    public PointD(int xIn, int yIn, long tIn){
        x = xIn;
        y = yIn;
        t = tIn;
    }
}
