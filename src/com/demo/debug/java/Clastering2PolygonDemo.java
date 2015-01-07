package com.demo.debug.java;

/**
 * Created by kevin on 2014/12/25.
 */

import com.alg.code.java.Optics;
import com.alg.code.java.Point;
import com.alg.code.java.Graham;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Clastering2PolygonDemo extends JFrame {
    static double eps = 20.0;
    static int minPts = 3;
    static int screenSize = 400;
    static int dataNum = 30;
    static int noiseNum = 50;
    static int[] ori = {100, 250, 30, 200, 70, 50, 100, 100, 70};
    static int clusterNum = ori.length / 3;
    Mypanel mypanel = null;
    ArrayList<Point> unsortedList = new ArrayList<Point>();
    ArrayList<Point> result = new ArrayList<Point>();

    /**
     * @ Description: Constructor of class Optics
     * @ Param unsortedList: Input cluster of 2D points
     * @ Param eps: Maximum distance required to get belonging points of some cluster
     * @ Param minPts: Minimum required number of points used to construct some cluster
     * @ Param debug: Debug switch specifically for distance calculation
     * @ Return: None
     * @ Throws: None
     */
    public static ArrayList<Point> initTestData() {
        ArrayList<Point> out = new ArrayList<Point>();
        Random r = new Random();
        //data set
        for(int i = 0; i < clusterNum; i++){
            for(int j = 0; j < dataNum; j++){
                Point g = new Point();
                double x = ori[3 * i] + ori[3 * i + 2] * r.nextDouble();
                double y = ori[3 * i + 1] + ori[3 * i + 2] * r.nextDouble();
                g.setLng(x);
                g.setLat(y);
                out.add(g);
            }
        }

        //noise set
        for(int i = 0; i < noiseNum; i++){
            Point g = new Point();
            double x = screenSize * r.nextDouble();
            double y = screenSize * r.nextDouble();
            g.setLng(x);
            g.setLat(y);
            out.add(g);
        }

        return out;
    }

    class Mypanel extends JPanel {


        /**
         * @ Description: Constructor of class Optics
         * @ Param unsortedList: Input cluster of 2D points
         * @ Param eps: Maximum distance required to get belonging points of some cluster
         * @ Param minPts: Minimum required number of points used to construct some cluster
         * @ Param debug: Debug switch specifically for distance calculation
         * @ Return: None
         * @ Throws: None
         */
        public void paint(Graphics g){
            Random r = new Random();
            int grid = screenSize / (clusterNum * dataNum + noiseNum);
            grid = 4;

            // draw original
            for(Point p : unsortedList){
                g.setColor(Color.black);
                g.drawOval((int)(p.getLng() - 2.5), (int)(p.getLat() - 2.5), 5, 5);
            }

            //draw grid
            if(false){
                for(int i = 0; i <40; i++){
                    g.setColor(Color.black);
                    g.drawLine(i * 10, 0, i *10, 400);
                    g.drawLine(0, i * 10, 400, i *10);
                }
            }

            //draw processing
            if(false){
                for(int i = 0; i < result.size(); i++){
                    Point p = result.get(i);
                    g.setColor(Color.green);

                    double rd = p.getReachDist();
                    if(rd == -1){
                        g.drawLine(i * grid, 0, i * grid, 300);
                        g.drawOval(i * grid, 300, 5, 5);

                    }
                    else{
                        g.drawLine(i * grid, 0, i * grid, (int)rd);
                        g.drawOval(i * grid, (int)rd, 5, 5);
                    }
                }
            }

            //draw cluster point
            if(true){
                for(Point p1 : result){
                    if(p1.getReachDist() == -1){
                        g.setColor(Color.red);
                    }
                    else{
                        g.drawOval((int)(p1.getLng() - 2.5), (int)(p1.getLat() - 2.5), 5, 5);
                    }
                }
            }

            // draw cluster outline --- circle
            if(true){
                ArrayList<Point> set = new ArrayList<Point>();
                int clusterIndex = 0;
                for(Point p2 : result){
                    if(p2.getReachDist() == -1){
                        if(set.size() != 0){
                            clusterIndex ++;
                            System.out.println("clusterIndex: " + clusterIndex);
                            Point central = getCentral(set);
                            ArrayList<Point> outline = getOutline(set);

                            double r0 = 2 * central.getReachDist() + 5;
                            int rr = (int)r0;
                            g.setColor(Color.blue);
                            int x = (int)(central.getLng() - r0 / 2);
                            int y = (int)(central.getLat() - r0 / 2);
                            g.drawOval(x, y, rr, rr);
                            set.clear();
                        }
                    }
                    else{
                        set.add(p2);
                    }
                }
            }

            // draw cluster outline ---- outline
            if(true){
                ArrayList<Point> set = new ArrayList<Point>();
                for(Point p2 : result){
                    if(p2.getReachDist() == -1) {
                        int sz = set.size();
                        if (sz != 0) {
                            Point[] PointArray = new Point[sz];
                            for (int i = 0; i < sz; i++) {
                                PointArray[i] = set.get(i);

                            }

                            Graham graham = new Graham(PointArray);
                            Stack<Point> outline1 = graham.getHull();
                            boolean firstDone = false;
                            g.setColor(Color.black);
                            Point ori0 = new Point();
                            Point end0 = new Point();
                            Point ori1 = new Point();
                            for (Point d : outline1) {
                                if (!firstDone) {
                                    ori1 = d;
                                    ori0 = d;
                                    firstDone = true;
                                    continue;
                                }

                                g.drawLine((int) ori1.getLng(),
                                        (int) ori1.getLat(),
                                        (int) d.getLng(),
                                        (int) d.getLat());
                                ori1 = d;
                                end0 = d;
                            }

                            g.drawLine((int) ori0.getLng(),
                                    (int) ori0.getLat(),
                                    (int) end0.getLng(),
                                    (int) end0.getLat());
                            set.clear();

                        }
                    }
                    else{
                        set.add(p2);
                    }
                }
            }
                System.out.println("Painting Done !");
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
    public static Point getCentral(ArrayList<Point> data){
        Point out = new Point();
        int size = data.size();
        double lng = 0;
        double lat = 0;
        double lngSum = 0;
        double latSum = 0;
        for(Point d : data){
            lngSum += d.getLng();
            latSum += d.getLat();
        }

        lng = lngSum / size;
        lat = latSum / size;
        out.setLng(lng);
        out.setLat(lat);

        double r = 0;
        double dist = 0;
        for(Point d: data){
            dist = Optics.calcDist(out, d);
            if(r < dist)
                r = dist;
        }
        out.setReachDist(r);
        return out;

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
    public static ArrayList<Point> getOutline(ArrayList<Point> data){
        ArrayList<Point> outline = new ArrayList<Point>();
        return outline;
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
    public static void main(String args[]){
        Clastering2PolygonDemo demo = new Clastering2PolygonDemo();
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
    public Clastering2PolygonDemo(){
        mypanel = new Mypanel();
        this.add(mypanel);
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        unsortedList = initTestData();

        Optics optics = new Optics(unsortedList, eps, minPts, true);
        result = optics.opticsCalc();

        System.out.println("Calculation DONE !");
    }

}