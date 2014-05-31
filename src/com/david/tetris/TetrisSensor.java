package com.david.tetris;

import java.awt.*;
import java.awt.image.BufferedImage;




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
	
	public TetrisSensor(Point [][] points, Rectangle rectangle, Robot r) throws AWTException {
        this.robot = r;
        screenImage = robot.createScreenCapture(rectangle);
		this.points = points;
	}
	
	public Robot getRobot() {
		 return robot;
	}
	
	public void getNewImage() {
		screenImage = robot.createScreenCapture(fullScreenRectangle);
	}

	public Point getBoardPosition() {		
		boolean foundFirst = false, foundSecond = false;
		int xFound = -1;
		int yFound = -1;

		all:
        for (int x = 0; x < screenImage.getWidth(); x++) {
		    for (int y = 0; y < screenImage.getHeight(); y++) {
				Color c = new Color(screenImage.getRGB(x, y));
				if (foundFirst) {
                    if (foundSecond) {
                        if (checkAll(c, 35)) {
							xFound = x;
							yFound = y;
							break all;
						} else if (checkAll (c, 26)) {
							continue;
						} else {
							foundFirst = false;
							foundSecond = false;
						}
					} else if (checkAll(c, 32)) {
						continue;
					} else if (checkAll(c, 26)) {
						foundSecond = true;
					} else {
						foundFirst = false;
					}
				}
				if (checkAll(c, 32)) {
					foundFirst = true;
				}
				
			}
		}
		if (xFound >= 0 && yFound >= 0) {
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
        System.out.println("Didn't find the board");
		return null;
	}

    public Point [][] getAbsGridPointArray(Point rootPoint) {
        Point [][] points  = getGridPointArray(rootPoint);
        for (Point [] row : points) {
            for (Point p : row) {
                p.x += rootPoint.x;
                p.y += rootPoint.y;
            }
        }
        return points;
    }
	
	public Point [][] getGridPointArray(Point rootPoint) {
		Point [][] points = new Point [20][10];
		
		if (rootPoint == null || rootPoint.x == -1 || rootPoint.y == -1) {
			return null;
		}
		//26 is the grid color
		int color = 26;
		int notBorderColor1 = 35;
		int notBorderColor2 = 32;
		
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
						System.out.println("Found Top Point! " + x + "," + (y+1));
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
