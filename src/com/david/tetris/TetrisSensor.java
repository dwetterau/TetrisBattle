package com.david.tetris;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;




public class TetrisSensor {
	
	private Robot robot;
	
	private Rectangle fullScreenRectangle;
	
	private BufferedImage screenImage;
	
	private Point [][] points;
	
	public TetrisSensor() throws AWTException {
		this.robot = new Robot();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		fullScreenRectangle = new Rectangle(screenSize);
		getNewImage();
	}
	
	public TetrisSensor(Point root, Point [][] points, int width, int height, Robot r) throws AWTException {
		screenImage = r.createScreenCapture(new Rectangle(root.x, root.y, width + 1, height + 1));
		this.points = points;
	}
	
	public Robot getRobot() {
		 return robot;
	}
	
	public void getNewImage() {
		screenImage = robot.createScreenCapture(fullScreenRectangle);
	}
	
	public Point getRootPositionStartScreen() {		
		boolean foundFirst = false, foundSecond = false;
		int xFound = -1;
		int yFound = -1;
		
		all:
		for (int y = 0; y < screenImage.getHeight(); y++) {
			for (int x = 0; x < screenImage.getWidth(); x++) {
				Color c = new Color(screenImage.getRGB(x, y));
				if (foundFirst) {
					if (foundSecond) {
						if (checkAll(c, 47)) {
							xFound = x;
							yFound = y;
							break all;
						} else if (checkAll (c, 34)) {
							continue;
						} else {
							foundFirst = false;
							foundSecond = false;
						}
					} else if (checkAll(c, 43)) {
						continue;
					} else if (checkAll(c, 34)) {
						foundSecond = true;
					} else {
						foundFirst = false;
					}
				}
				if (checkAll(c, 43)) {
					foundFirst = true;
				}
				
			}
		}
		if (xFound >= 0 && yFound >= 0) {
			foundFirst = false;
			for (int x = xFound; x >= 0; x--) {
				Color c = new Color(screenImage.getRGB(x, yFound));
				if (foundFirst) {
					if (checkAll(c, 255)) {
						System.out.println("Found the white background of the left edge.");
						xFound = x + 1;
						System.out.println(xFound+", "+yFound);
						break;
					} else if (!checkAll(c, 0)) {
						foundFirst = false;
					}
				} else if (checkAll(c, 0)) {
					System.out.println("Found black left edge");
					foundFirst = true;
				}
				if (x == 0) {
					xFound = -1;
					yFound = -1;
					System.out.println("Unable to find black side border.");
				}
			}
			if (xFound >= 0) {
				foundFirst = false;
				//go up until you find the top edge too.
				for (int y = yFound; y >= 0; y--) {
					Color c = new Color(screenImage.getRGB(xFound, y));
					if (foundFirst) {
						if (checkAll(c, 255)) {
							yFound = y + 1;
							System.out.println("Successfully got root point.");
							break;
						}
					} else if (checkAll(c, 0)) {
						foundFirst = true;
					} else {
						System.out.println("Didn't find black left edge after supposedly finding it.");
					}
					if (y == 0) {
						xFound = -1;
						yFound = -1;
						System.out.println("Unable to find top corner.");
					}
				}
			}
		}
		if (xFound >=0 && yFound >=0) {
			return new Point(xFound, yFound);
		}
		return null;
	}
	
	public Point getBoardPosition() {		
		boolean foundFirst = false, foundSecond = false;
		int xFound = -1;
		int yFound = -1;
		
		all:
		for (int y = 0; y < screenImage.getHeight(); y++) {
			for (int x = 0; x < screenImage.getWidth(); x++) {
				Color c = new Color(screenImage.getRGB(x, y));
				if (foundFirst) {
					if (foundSecond) {
						if (checkAll(c, 47)) {
							xFound = x;
							yFound = y;
							break all;
						} else if (checkAll (c, 34)) {
							continue;
						} else {
							foundFirst = false;
							foundSecond = false;
						}
					} else if (checkAll(c, 43)) {
						continue;
					} else if (checkAll(c, 34)) {
						foundSecond = true;
					} else {
						foundFirst = false;
					}
				}
				if (checkAll(c, 43)) {
					foundFirst = true;
				}
				
			}
		}
		if (xFound >= 0 && yFound >= 0) {
			foundFirst = false;
			for (int x = xFound; x >= 0; x--) {
				Color c = new Color(screenImage.getRGB(x, yFound));
				if (checkAll(c, 0)) {
					xFound = x;
					System.out.println("Found x side: "+xFound);
					break;
				} else if( x == 0) {
					xFound = -1;
				}
			}
			if (xFound != -1) {
				for (int y = yFound; y >= 0; y--) {
					Color c = new Color(screenImage.getRGB(xFound + 1, y));
					if (checkAll(c, 0)) {
						yFound = y;
						System.out.println("Found y side: "+yFound);
						break;
					} else if( y == 0) {
						yFound = -1;
					}
				}
			}
		}
		if (xFound >= 0 && yFound >= 0) {
			return new Point(xFound, yFound);
		}
		return null;
	}
	
	public Point [][] getGridPointArray(Point rootPoint) {
		Point [][] points = new Point [20][10];
		
		if (rootPoint == null || rootPoint.x == -1 || rootPoint.y == -1) {
			return null;
		}
		//34 is the grid color
		int color = 34;
		int notBorderColor1 = 47;
		int notBorderColor2 = 43;
		
		int widthMax = Math.min(screenImage.getWidth()-1, rootPoint.x + 500);
		int heightMax = Math.min(screenImage.getHeight()-1, rootPoint.y + 500);
		
		all:
		for (int y = rootPoint.y; y < heightMax; y++) {
			for (int x = rootPoint.x; x < widthMax; x++) {
				Color c = new Color(screenImage.getRGB(x, y));
				if (checkAll(c, color)) {
					Color right = new Color(screenImage.getRGB(x+1, y));
					Color down = new Color(screenImage.getRGB(x, y+1));
					Color catty = new Color(screenImage.getRGB(x+1, y+1));
					if (checkAll(right, color) && checkAll(catty, color) &&
						(checkAll(down, notBorderColor1) || checkAll(down, notBorderColor2))) {
						System.out.println("Found Top Point!" + x + "," + (y+1));
						points[0][0] = new Point (x-1, y+2);
						break all;
					}
				}
			}
		}
		
		if (points[0][0] != null) {
			int baseX = points[0][0].x;
			int baseY = points[0][0].y;
			for (int r = 0; r < points.length; r++) {
				for (int c = 0; c < points[r].length; c++) {
					points[r][c] = new Point(baseX + 18*c, baseY + 18*r);
				}
			}
		}
		
		return points;
	}
	
	public Color [][] getColorGrid() {
		return getColorGrid(points);
	}
	
	public Color [][] getColorGrid(Point [][] points) {
		Color [][] colors = new Color [20][10];
		for (int r = 0; r < points.length; r++) {
			for (int c = 0; c < points[r].length; c++) {
				if (points[r][c] == null) { 
					return null;
				}
				colors[r][c] = new Color(screenImage.getRGB(points[r][c].x, points[r][c].y));
			}
		}
		return colors;
	}
	
	private boolean checkAll(Color c, int target) {
		return c.getRed() == target && c.getGreen() == target && c.getBlue() == target;
	}
}