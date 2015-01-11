package com.demo.debug.java;

/**
 * Created by kevin on 2015/1/2.
 * This class is for path related demos, which now mainly give a UI to create 7 different datasets for
 * path related algorithm test
 */

import java.awt.Graphics;
import java.awt.Event;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class PathRelatedDemo extends java.applet.Applet //implement MouseListener
{
    int MAX_USER = 7;
    Color[] c = {Color.red, Color.green, Color.blue, Color.orange, Color.white, Color.cyan, Color.pink};
    int[] starts = {0, 0, 0, 0, 0, 0, 0};
    int index = 0;
    int date = 0;
    ArrayList<PointD> psAll = new ArrayList<PointD>();

    /**
     * @ Description: Applet init function
     * @ Param: None
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

    /**
     * @ Description: Key down handler
     * @ Param evt: Event passed in
     * @ Param x: Key value passed in
     * @ Return: Whether the event is processed
     * @ Throws: None
     */
    public boolean keyDown(Event evt, int x){
        System.out.println("keyDown");

        if(x == 'a'){
            System.out.println("Another day's trajectory");
            psAll.clear();
            index = 0;
            date ++;
            repaint();
        }
        else if(x == ' '){
            System.out.println("Switch to another data set !");
            ++index;
            if(index < MAX_USER){
                starts[index] = psAll.size();
                repaint();
            }
        }
        return true;
    }

    /**
     * @ Description: Mouse down handler
     * @ Param evt: Event passed in
     * @ Param x: x coordinate of mouse down position
     * @ Param y: y coordinate of mouse down position
     * @ Return: Whether the event is processed
     * @ Throws: None
     */
    public boolean mouseDown(Event evt, int x, int y){
        System.out.println("mouseDown " + x + " " + y);

        if(index < MAX_USER){
            PointD p = new PointD(x, y, evt.when);
            psAll.add(p);
            repaint();
        }
        return true;
    }

    /**
     * @ Description: Used to update graphics
     * @ Param eps: Maximum distance required to get belonging points of some cluster
     * @ Return: None
     * @ Throws: None
     */
    public void update(Graphics g){
        g.setColor(Color.black);
        g.fillRect(0, 0, 1000, 1000);
        paint(g);
    }

    /**
     * @ Description: Paint the graphics
     * @ Param g: Graphics handler
     * @ Return: None
     * @ Throws: None
     */
    public void paint(Graphics g){
        g.setColor(Color.red);
        int x = 0, y = 0;

        if(index < MAX_USER){
            if(index == 0){
                g.setColor(c[index]);
                for(int i = 0; i < psAll.size(); i++){
                    x = psAll.get(i).x;
                    y = psAll.get(i).y;
                    g.drawLine(x - 5, y - 5, x + 5, y + 5);
                    g.drawLine(x + 5, y - 5, x - 5, y + 5);

                }
            }
            else{
                for(int i = 0; i < index; i++){
                    g.setColor(c[i]);
                    int start = starts[i];
                    int end = starts[i + 1];
                    for(int j = start; j < end; j++){
                        x = psAll.get(j).x;
                        y = psAll.get(j).y;
                        g.drawLine(x - 5, y - 5, x + 5, y + 5);
                        g.drawLine(x + 5, y - 5, x - 5, y + 5);
                    }
                }
                g.setColor(c[index]);
                for(int i = starts[index]; i < psAll.size(); i++){
                    x = psAll.get(i).x;
                    y = psAll.get(i).y;
                    g.drawLine(x - 5, y - 5, x + 5, y + 5);
                    g.drawLine(x + 5, y - 5, x - 5, y + 5);
                }
            }
        }
    }

}
