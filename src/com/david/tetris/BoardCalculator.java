package com.david.tetris;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class BoardCalculator {

	private Color [][] oldBoard;
	
	private Color [][] board;
	
	public BoardCalculator(Color [][] board) {
		this.oldBoard = board;
		this.board = board;
	}
	
	public boolean addPiece(Piece p) {
		
		int col = p.getRoot().y;
		int currentR = 0;
		
		Point [] points = p.getBlocks()[p.getIndex()];
		boolean good = false;
		boolean outside = false;
		all:
		while (!good && currentR++ < board.length) {
			boolean allGood = true;
			boolean touching = false;
			for (Point point : points) {
				//System.out.println(currentR);
				if (point.x + currentR >= board.length || point.x + currentR  < 0) {
					allGood = false;
					break;
				}
				if (col+point.y >= board[0].length || col+point.y < 0) {
					outside = true;
					break all;
				}
				if (PieceSensor.isPieceColor(board[point.x + currentR][col+point.y])) {
					allGood = false;
				}
				if (point.x + currentR == board.length-1 || PieceSensor.isPieceColor(board[point.x + currentR +1][col + point.y])) {
					touching = true;
				}
			}
			if (allGood && touching) {
				good = true;
			}    
		}
		if (!good || outside) {
			return false;
		}		
		for (Point point : points) {
			board[point.x + currentR][col + point.y] = PieceSensor.J_PIECE_COLOR;
		}
		
		return true;
	}
	
	public boolean cleanBoard() {
		ArrayList<Point> deadPoints = new ArrayList<Point>();
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				if (board[r][c].equals(PieceSensor.DEST_COLOR)) {
					deadPoints.add(new Point(r,c));
				}
			}
		}
		for (Point p : deadPoints) {
			for (int i = 0; i <= p.x; i++) {
				board[i][p.y] = new Color(47,47,47);
			}
		}
		return deadPoints.size() > 0;
	}
	
	public double scoreBoard() {
		//Metrics:
		//average height
		//max height
		//holes
		//lines cleared
		//bombs exposed (these two can be added later)
		//bombs detonated
		
		
		int [] heights = new int [board[0].length];
		int holes = 0;
		
		for (int c = 0; c < board[0].length; c++) {
			boolean found = false;
			for (int r = 0; r < board.length; r++) {
				boolean notEmpty = PieceSensor.isPieceColor(board[r][c]);
				if (found && !notEmpty) {
					holes++;
				}
				else if (!found && notEmpty) {
					heights[c] = board.length - r;
					found = true;
				} 
			}
		}
		
		int max = Integer.MIN_VALUE;
		double sum = 0;
		for (int i = 0; i < heights.length; i++) {
			if (heights[i] > max) {
				max = heights[i];
			}
			sum += heights[i];
		}
		
		int bumpiness = 0;
		for (int i = 1; i < board[0].length; i++) {
			bumpiness += Math.abs(heights[i] - heights[i-1]);
		} 
		
		int valleys = 0;
		for (int c = 0; c < board[0].length; c++) {
			for (int r = 2; r < board.length; r++) {
				if (!PieceSensor.isPieceColor(board[r][c])) {
					if (!PieceSensor.isPieceColor(board[r-1][c]) && !PieceSensor.isPieceColor(board[r-2][c])) {
						boolean goodRight = (c==board[0].length-1) || heights[c+1] >= board.length - (r-2);
						boolean goodLeft = (c==0) || heights[c-1]>= board.length - (r-2); 
						if (goodRight && goodLeft) {
							valleys++;
						}
					}
				}
			}
		}
		
		double average = sum/board[0].length;
		int linesCleared = 0;
		
		for (int r = 0; r < board.length; r++) {
			boolean clearedLine = true;
			for (int c = 0; c < board[0].length; c++) {
				if (!PieceSensor.isPieceColor(board[r][c])) {
					clearedLine = false;
				}
			}
			if (clearedLine) {
				linesCleared++;
			}
		}
		
		/*return .2 * max + 
				3 * average + 
				.03125 * holes - 
				.5 * (linesCleared - 1);*/
		//72, 75, 442, 56, 352
		return 72 * max +
			   75 * average +
			  442 * holes +
		 	   56 * bumpiness +
			  352 * valleys -
              ((linesCleared > 0) ? 10000 * (linesCleared) : 0);
	}
	
	public Color [][] getBoard() {
		 return board;
	}
}
