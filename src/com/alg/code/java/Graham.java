package com.alg.code.java;

/**
 * Created by kevin on 2014/12/25.
 */

import java.util.Arrays;
import java.util.Stack;

/**
  Graham algorithm is used to find the outline point of a cluster of 2D points
 */
public class Graham{
    private Stack<Point> hull = new Stack<Point>();  //outline points

    /**
     * @ Description: Constructor of Graham class
     * @ Param points: A cluster of 2D points
     * @ Return: None
     * @ Throws: None
     */
    public Graham(Point[] points){
        int len =points.length;
        Arrays.sort(points);
        Arrays.sort(points, 1, len, points[0].POLAR_ORDER);
        hull.push(points[0]); //first extreme point

        int p1;
        for(p1 =1; p1 < len; p1++)
            if(!points[0].equals(points[p1]))
                break;

        if(p1 == len)
            return;  //all points equal

        int p2;
        for(p2 = p1 + 1; p2 < len; p2++)
            if(Point.counterClockwise(points[0], points[p1], points[p2]) != 0)
                break;
        hull.push(points[p2 - 1]);  //second extreme point

        //Graham scan
        for(int i = p2; i < len; i++){
            Point top = hull.pop();
            while(Point.counterClockwise(hull.peek(), top, points[i]) <= 0)
                top = hull.pop();

            hull.push(top);
            hull.push(points[i]);
        }
    }

     /**
     * @ Description: Get private "hull" for outside caller
     * @ Param points: None
     * @ Return: outline points for the input cluster of 2D points
     * @ Throws: None
     */
    public Stack<Point> getHull(){
        return hull;
    }

}
