package com.david.tetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Overlay
{
    public static void main(String[] args) { 
        JFrame frame = new JFrame("Tetris Overlay");
        //frame.setBackground(new Color(0, 0, 0, 0));
        frame.setSize(1920, 1080);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        // Without this, the window is draggable from any non transparent
        // point, including points  inside textboxes.
        frame.getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
        Canvas c = new Canvas() {
        	@Override
        	public void update(Graphics g) {
        		paint(g);
        	}
        	
        	@Override
        	public void paint(Graphics g) {
        		g.drawString("Hello World", 100, 100);
        	}
        };
        
        frame.setOpacity(0);
        
        try {
        	   Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
        	   Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
        	   mSetWindowOpacity.invoke(null, frame, Float.valueOf(0.75f));
        	} catch (NoSuchMethodException ex) {
        	   ex.printStackTrace();
        	} catch (SecurityException ex) {
        	   ex.printStackTrace();
        	} catch (ClassNotFoundException ex) {
        	   ex.printStackTrace();
        	} catch (IllegalAccessException ex) {
        	   ex.printStackTrace();
        	} catch (IllegalArgumentException ex) {
        	   ex.printStackTrace();
        	} catch (InvocationTargetException ex) {
        	   ex.printStackTrace();
        	}
        
        //frame.getContentPane().add(c);
        frame.setVisible(true);
        
        
    }
}