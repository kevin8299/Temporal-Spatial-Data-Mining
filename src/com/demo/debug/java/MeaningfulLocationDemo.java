package com.demo.debug.java;

/**
 * Created by kevin on 2015/1/2.
 */
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class MeaningfulLocationDemo extends java.applet.Applet {    //implement MouseListener
        int num = 0;
        int px, py;
        ArrayList<PointD> ps = new ArrayList<PointD>();

        static class StayPoint_Debug {
            int lng = 0, lat = 0, num = 1;
            long tArr = 0, tLea = 0;
            String info = "";

            StayPoint_Debug(int lng, int lat, long tArr, long tLea) {
                this.lng = lng;
                this.lat = lat;
                this.tArr = tArr;
                this.tLea = tLea;
            }
        }
            double dist(double lng0, double lat0, double lng1, double lat1){
                double disX = lng0 - lng1;
                double disY = lat0 - lat1;

                return Math.sqrt(disX * disX + disY * disY);
            }

            PointD prev = new PointD();
            ArrayList<PointD> points = new ArrayList<PointD>();
            ArrayList<StayPoint_Debug> sps = new ArrayList<StayPoint_Debug>();
            int thetaD = 50;
            int thetaT = 1000;

            public void detectSPs_debugDemo(PointD d){
                if(prev.x == 0) prev = d;
                else{
                    double dist = dist(prev.x, prev.y, d.x, d.y);
                    if(dist > thetaD){
                        double deltaT = Math.abs(d.t - prev.t);

                        if(deltaT > thetaT){
                            int lngSum = prev.x, latSum = prev.y;
                            long tArr, tLea;
                            int meanLng, meanLat;

                            if(points.size() > 0){
                                lngSum = 0;
                                latSum = 0;
                                tArr = points.get(0).t;
                                tLea = points.get(0).t;

                                for(int i = 0; i < points.size(); i++){
                                    System.out.println("To compute SP: " + points.get(i).x + " " + points.get(i).y);
                                    lngSum += points.get(i).x;
                                    latSum += points.get(i).y;

                                    if(tArr > points.get(i).t)
                                        tArr = points.get(i).t;

                                    if(tLea < points.get(i).t)
                                        tLea = points.get(i).t;
                                }

                                meanLng = lngSum / points.size();
                                meanLat = latSum / points.size();

                            }
                            else{
                                tArr = prev.t;
                                tLea = prev.t;
                                meanLng = prev.x;
                                meanLat = prev.y;
                            }

                            System.out.println("Computed SP : " + meanLng + " " + meanLat + " " + tArr + " " + tLea);
                            StayPoint_Debug sp = new StayPoint_Debug(meanLng, meanLat, tArr, tLea);
                            sps.add(sp);
                        }
                        prev = d;
                        points.clear();
                    }
                    else{
                        points.add(d);

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
        public void init(){
            //this.addMouseListener(this);
            System.out.println("init ...");
        }
/*
	public void mouseEntered(MouseEvent e){
		System.out.println("mouseEntered");
	}

	public void mouseReleased(MouseEvent e){
		System.out.println("mouseReleased");
	}

	public void mouseExited(MouseEvent e){
		System.out.println("mouseExited");
	}

	public void mousePressed(MouseEvent e){
		System.out.println("mousePressed");
	}

	public void mouseClicked(MouseEvent e){
		System.out.println("mouseClicked");
	}
*/

        public boolean keyDown(Event evt, int x){
            System.out.println("keyDown");
            return true;
        }

        public boolean mouseDown(Event evt, int x, int y){
            System.out.println("mouseDown " + x + " " + y);
            PointD p = new PointD(x, y, evt.when);
            ps.add(p);
            String d = "";
            for(int i = 0; i < ps.size(); i++)
                d += ps.get(i).x + "_" + ps.get(i).y + "_0_" + ps.get(i).t + ":";
            System.out.println("Created Dataset: " + d + "\n");
            detectSPs_debugDemo(p);

            px = x;
            py = y;
            repaint();
            return true;
        }

        public void update(Graphics g){
            g.setColor(Color.black);
            g.fillRect(0, 0, 1000, 1000);
            paint(g);
        }

        public void paint(Graphics g){
            g.setColor(Color.red);
            int x = 0, y = 0;

            for(int i = 0; i < ps.size(); i++){
                x = ps.get(i).x;
                y = ps.get(i).y;
                g.drawLine(x - 5, y - 5, x + 5, y + 5);
                g.drawLine(x + 5, y - 5, x - 5, y + 5);
            }

            g.setColor(Color.green);

            for(int i = 0; i < this.sps.size(); i++){

                StayPoint_Debug one = this.sps.get(i);
                System.out.println("SP: " + one.lng + " " + one.lat);
                g.drawOval(one.lng - this.thetaD / 2,
                        one.lat - this.thetaD / 2,
                        this.thetaD, this.thetaD);
            }

        }

    }

