package com.david.tetris;

import java.awt.*;

public class TestTetrisSensor {
	
	public static void main(String ... args) throws AWTException {
		
		TetrisSensor sensor = new TetrisSensor();
		Point p = sensor.getBoardPosition();
		int count = 0;
		
		if (p != null) {
			long start = -System.nanoTime();
			Point [][] points = sensor.getGridPointArray(p);
            //Point [][] points = sensor.getAbsGridPointArray(p);
            System.out.println("Got pieces in: " + ((start + System.nanoTime()) / 1000.0));
			Color [][] colors = sensor.getColorGrid(points);

			PieceSensor pieceSensor = new PieceSensor(colors);
			Piece piece = pieceSensor.getCurrentPiece();
			if (piece != null) {
				System.out.println(piece+"\n\n");
				BoardCalculator calculator = new BoardCalculator(colors);
				boolean cleaned = calculator.cleanBoard();
				if (!cleaned) {
					System.out.println("didn't clean board.");
				}
				boolean added = calculator.addPiece(piece);
				
				if (!added) {
					System.out.println("didn't add?");
				}
				
				colors = calculator.getBoard();
				/*for (int r = 0; r < 20; r++) {
					for (int c = 0; c < 10; c++) {
						System.out.print(colors[r][c].getRed()+"-"+colors[r][c].getGreen()+ "-" + colors[r][c].getBlue() +"\t");
					}
					System.out.println();
				}*/
				
				
			} else {
				//System.out.println("Unable to find current piece");
			}
		} 		
	}
}
