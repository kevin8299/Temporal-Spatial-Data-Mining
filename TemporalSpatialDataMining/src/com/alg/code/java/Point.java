package com.alg.code.java;

/**
 * Created by kevin on 2014/12/25.
 * Point class is used for algorithm of Optics and Gramham
 */

import java.util.Comparator;

public class Point implements Comparable<Point> {
    private double lng = 0;
    private double lat = 0;
    private boolean processed = false;
    private double reachDist = -1;
    private double coreDist = -1;

    private double tArr = 0;
    private double tLea = 0;
    private String userId = "";
    private double date = 0;

    /**
     * @ Description: Constructor of class Point
     * @ Param: None
     * @ Return: None
     * @ Throws: None
     */
    public Point(){}

    /**
     * @ Description: Constructor of class Point
     * @ Param lngIn: Point's longitude
     * @ Param latIn: Point's latitude
     * @ Param userIdIn: To whom the point belongs
     * @ Param tArrIn: The arrive time into the point
	 * @ Param tLeaIn: The leave time into the point
	 * @ Param dateIn: Which date the point is created
     * @ Return: None
     * @ Throws: None
     */
    public Point(double lngIn, double latIn, String userIdIn, double tArrIn, double tLeaIn,double dateIn){
        lng = lngIn;
        lat = latIn;
        userId = userIdIn;
        tArr = tArrIn;
        tLea = tLeaIn;
        date = dateIn;
    }

    /**
     * @ Description: Constructor of class Point, used specifically for time density clustering
     * @ Param lngIn: Point's longitude
     * @ Param latIn: Point's latitude
     * @ Return: None
     * @ Throws: None
     */
    public Point(double lngIn, double latIn){
        lng = lngIn;
        lat = latIn;
    }

    /**
     * @ Description: Override the "compareTo" function required by "Comparable" 
     * @ Param that: The point to be compared
     * @ Return: Compared result
     * @ Throws: None
     */
    @Override
    public int compareTo(Point that){
        if(this.getLat() < that.getLat())
            return -1;
        if(this.getLat() > that.getLat())
            return +1;
        if(this.getLng() < that.getLng())
            return -1;
        if(this.getLng() > that.getLng())
            return +1;
        return 0;
    }

    public final Comparator<Point> POLAR_ORDER =  new PolarOrder();

    /**
     * @ Description: Help to sort points in the counter clockwise order
     * @ Param a: One point
     * @ Param b: The second point
     * @ Param c: The third point
     * @ Return: The tag of order for the 3 points
     * @ Throws: None
     */
    public static int counterClockwise(Point a, Point b, Point c){
        double area2 = (b.lng - a.lng) * (c.lat - a.lat) - (b.lat - a.lat) * (c.lng - a.lng);
        if(area2 < 0)
            return -1;
        else if(area2 > 0)
            return +1;
        else
            return 0;
    }

	/**
		This class helps to sort points 
	*/
    private class PolarOrder implements Comparator<Point> {
        public int compare(Point q1, Point q2){
            double dx1 = q1.getLng() - lng;
            double dy1 = q1.getLat() - lat;
            double dx2 = q2.getLng() - lng;
            double dy2 = q2.getLat() - lat;

            if(dy1 >= 0 && dy2 < 0)
                return -1;  //q1 above, q2 below
            else if(dy2 >= 0 && dy1 < 0)
                return +1; //q2 above, q1 below
            else if(dy1 == 0 && dy2 == 0){  // 3 collinear and horizontal
                if(dx1 >= 0 && dx2 < 0)
                    return -1;
                else if(dx2 >= 0 && dx1 <0)
                    return +1;
                else
                    return 0;
            }
            else
                return -counterClockwise(Point.this, q1, q2);  // both above and below
        }
    }


    /**
     * @ Description: The following functions is for outside caller to retrieve dataset
     * @ Param: None
     * @ Return: None
     * @ Throws: None
     */
    public double getLng(){
        return lng;
    }

    public void setLng(double lng){
        this.lng = lng;
    }

    public double getLat(){
        return lat;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public boolean isProcessed(){
        return processed;
    }

    public void setProcessed(boolean processed){
        this.processed = processed;
    }

    public double getCoreDist(){
        return coreDist;
    }

    public void setCoreDist(double coreDist){
        this.coreDist = coreDist;
    }

    public double getReachDist(){
        return reachDist;
    }

    public void setReachDist(double reachDist){
        this.reachDist = reachDist;
    }

    public double getTArr(){
        return tArr;
    }

    public void setTArr(double tArr){
        this.tArr = tArr;
    }

    public double getTLea(){
        return tLea;
    }

    public void setTLea(double tLea){
        this.tLea = tLea;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public double getDate(){
        return date;
    }

    public void setDate(double date){
        this.date = date;
    }


}
