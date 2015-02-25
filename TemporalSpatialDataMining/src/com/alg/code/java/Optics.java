package com.alg.code.java;

import java.util.ArrayList;

/**
 * Created by kevin on 2014/12/24.
 * Optics algorithm is used to cluster a cluster of 2D points based on density
 */

public class Optics {
    private ArrayList<Point> unsortedList;
    private double eps;
    private int minPts;
    private static boolean debug;

    /**
    * @ Description: Constructor of class Optics
    * @ Param unsortedList: Input cluster of 2D points required to be clustered
    * @ Param eps: Maximum distance required to get belonging points of some cluster
    * @ Param minPts: Minimum required number of points used to construct some cluster
    * @ Param debug: Debug switch specifically for distance calculation
    * @ Return: None
    * @ Throws: None
    */
    public Optics(ArrayList<Point> unsortedList, double eps, int minPts, boolean debug){
        this.unsortedList = unsortedList;
        this.eps = eps;
        this.minPts = minPts;
        this.debug = debug;
    }

    /**
    * @ Description: Get the calculated clusters 
    * @ Param points: None
    * @ Return: The calculated out clusters
    * @ Throws: None
    */
    public ArrayList<ArrayList<Point>> clusters(){
        ArrayList<ArrayList<Point>> out = new ArrayList<ArrayList<Point>>();
        ArrayList<Point> result = opticsCalc();

        ArrayList<Point> set = new ArrayList<Point>();
        for(Point p: result){
            if(p.getReachDist() == -1){
                int sz = set.size();
                if(sz != 0){
                    ArrayList<Point> set1 = new ArrayList<Point>();
                    for(Point p1: set)
                        set1.add(p1);
                    out.add(set1);
                    set.clear();
                }
            }
            else{
                set.add(p);
            }
        }

        if(set.size() > 0){
            ArrayList<Point> set2 = new ArrayList<Point>();
            for(Point p2: set)
                set2.add(p2);
            out.add(set2);
            set.clear();
        }

        return out;
    }

    /**
    * @ Description: Main implementation of Optics alg
    * @ Param points: None
    * @ Return: The tagged dataset for further clustering
    * @ Throws: None
    */
    public ArrayList<Point> opticsCalc(){
        int dataSize = unsortedList.size();
        ArrayList<Point> sortedList = new ArrayList<Point>();
        if(dataSize < minPts){
            System.out.println("Alarm !  Input data size is less than minPts: " + minPts );
        }
        else{
            for(int i = 0; i < unsortedList.size(); i++){
                Point point = unsortedList.get(i);
                if(!point.isProcessed()){
                    ArrayList<Point> neighbors = getNeighbors(point, unsortedList, eps);
                    point.setProcessed(true);
                    sortedList.add(point);
                    ArrayList<Point> priorityQueue = new ArrayList<Point>();
                    if(calcCoreDist(point, unsortedList, eps, minPts) != -1){
                        updateQueue(point, neighbors, priorityQueue, unsortedList, eps, minPts);
                        for(int j = 0; j < priorityQueue.size(); j++){
                            Point queue_point = priorityQueue.get(j);
                            if(!queue_point.isProcessed()){
                                ArrayList<Point> queue_neighbors = getNeighbors(queue_point, unsortedList, eps);
                                queue_point.setProcessed(true);
                                sortedList.add(queue_point);
                                if(calcCoreDist(queue_point, unsortedList, eps, minPts) != -1){
                                    updateQueue(queue_point, queue_neighbors, priorityQueue, unsortedList, eps, minPts);
                                }
                            }
                        }
                    }
                }
            }
        }
        return sortedList;
    }

    /**
    * @ Description: Calculate the neighbor points around one points
    * @ Param point: The centered point
	* @ Param allList: ALl datasets
	* @ Param eps: The predefined distance to find neighbor points
    * @ Return: The centered point's neighbors
    * @ Throws: None
    */
    private ArrayList<Point> getNeighbors(Point point, ArrayList<Point> allList, double eps){
        ArrayList<Point> out = new ArrayList<Point>();
        for(Point g: allList){
            if(!g.equals(point) && calcDist(g, point) < eps)
                out.add(g);
        }
        return out;

    }

    /**
    * @ Description: Calculate the core distance of input point
    * @ Param p: The point to calculate core distance
	* @ Param allList: All datasets
	* @ Param eps: The predefined distance to find neighbor points
	* @ Param minPts: Minimum required number of points used to construct some cluster
    * @ Return: Calculated out core distance
    * @ Throws: None
    */
    private double calcCoreDist(Point p, ArrayList<Point> allList, double eps, int minPts){
        double dist = -1;
        ArrayList<Point> neighbors = getNeighbors(p, allList, eps);
        if(neighbors.size() >= minPts){
            dist = eps;
            for(Point g: neighbors){
                double d = calcDist(g, p);
                if(d < dist)
                    dist = d;
            }
        }
        return dist;

    }

    /**
    * @ Description: Calculate the priority queue
    * @ Param point: The point to update the priority queue
	* @ Param neighbors: Neighbor points of "point"
	* @ Param priorityQueue: Priority queue in Optics
	* @ Param allList: All datasets
	* @ Param eps: The predefined distance to find neighbor points
	* @ Param minPts: Minimum required number of points used to construct some cluster
    * @ Return: outline points for the input cluster of 2D points
    * @ Throws: None
    */
    private void updateQueue(Point point, ArrayList<Point> neighbors, ArrayList<Point> priorityQueue, ArrayList<Point> allList, double eps, int minPts){
        double coreDist = calcCoreDist(point, allList, eps, minPts);
        for(Point p: neighbors){
            if(!p.isProcessed()){
                double reachDist = Math.max(coreDist, calcDist(p, point));
                if(p.getReachDist() == -1){
                    p.setReachDist(reachDist);
                    priorityQueue.add(p);
                }
                else{
                    if(reachDist < p.getReachDist()){
                        p.setReachDist(reachDist);
                        priorityQueue.remove(p);
                        priorityQueue.add(p);
                    }
                }
            }
        }
    }

    /**
    * @ Description: Calculate the distance of 2 points 
    * @ Param lng1: One point's longitude
	* @ Param lat1: One point's latitude
	* @ Param lng2: The other point's longitude
	* @ Param lat2: The other point's latitude
	* @ Param debug: Swith for 2 different distance calculation method
    * @ Return: The calculated out distance of 2 points
    * @ Throws: None
    */
    public static double geoDist(double lng1, double lat1, double lng2, double lat2, Boolean debug){
        if(debug){
            double disX = lng1 - lng2;
            double disY = lat1 - lat2;
            return Math.sqrt(disX * disX + disY * disY);
        }
        else{
            double EARTH_RADIUS = 6378137.0;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLng = Math.toRadians(lng2 - lng1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2))
                    * Math.sin(dLng / 2)
                    * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double dist = EARTH_RADIUS * c;
            return dist;

        }
    }

    /**
    * @ Description: Calculate the distance of 2 points 
    * @ Param p1: One point
	* @ Param p2: The other point
    * @ Return: The calculated out distance of 2 points
    * @ Throws: None
    */
    public static double calcDist(Point p1, Point p2){
        return geoDist(p1.getLng(), p1.getLat(), p2.getLng(), p2.getLat(),debug);
    }
}
