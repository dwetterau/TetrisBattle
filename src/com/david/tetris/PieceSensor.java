package com.david.tetris;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class PieceSensor {
	
	private Color [][] board;
	
	public static final Color DEST_COLOR = new Color(102, 102, 102);
	
	public static final Color J_PIECE_COLOR = new Color(68, 124, 255);
	
	public static final Color L_PIECE_COLOR = new Color(255, 156, 35);
	
	public static final Color BLOCK_PIECE_COLOR = new Color(255, 217, 59);
	
	public static final Color LINE_PIECE_COLOR = new Color(44, 209, 255);
	
	public static final Color RIGHT_PIECE_COLOR = new Color(255, 67, 92);
	
	public static final Color LEFT_PIECE_COLOR = new Color(134, 234, 51);
	
	public static final Color T_PIECE_COLOR = new Color(232, 76, 201);
	
	public static final Piece J_PIECE = new Piece(new Point(0,0), new Point[][] {
		{ new Point(0,0), new Point(1,0), new Point(1,1), new Point(1,2) },
		{ new Point(0,0), new Point(0,1), new Point(1,0), new Point(2,0) },
		{ new Point(0,0), new Point(0,1), new Point(0,2), new Point(1,2) },
		{ new Point(0,1), new Point(1,1), new Point(2,0), new Point(2,1) }
	}, 0);
	
	public static final Piece L_PIECE = new Piece(new Point(0,0), new Point[][] {
		{ new Point(0,2), new Point(1,0), new Point(1,1), new Point(1,2) },
		{ new Point(0,0), new Point(1,0), new Point(2,0), new Point(2,1) },
		{ new Point(0,0), new Point(0,1), new Point(0,2), new Point(1,0) },
		{ new Point(0,0), new Point(0,1), new Point(1,1), new Point(2,1) }
	}, 0);
	
	public static final Piece BLOCK_PIECE = new Piece(new Point(0,0), new Point[][] {
		{ new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,1) }
	}, 0);
	
	public static final Piece LINE_PIECE = new Piece(new Point(0,0), new Point[][] {
		{ new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3) },
		{ new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0) }
	}, 0);
	
	public static final Piece RIGHT_PIECE = new Piece(new Point(0,0), new Point[][] {
		{ new Point(0,0), new Point(0,1), new Point(1,1), new Point(1,2) },
		{ new Point(0,1), new Point(1,0), new Point(1,1), new Point(2,0) }
	}, 0);
	
	public static final Piece LEFT_PIECE = new Piece(new Point(0,0), new Point[][] {
		{ new Point(0,1), new Point(0,2), new Point(1,0), new Point(1,1) },
		{ new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1) }
	}, 0);
	
	public static final Piece T_PIECE = new Piece(new Point(0,0), new Point[][] {
		{ new Point(0,1), new Point(1,0), new Point(1,1), new Point(1,2) },
		{ new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,0) },
		{ new Point(0,0), new Point(0,1), new Point(0,2), new Point(1,1) },
		{ new Point(0,1), new Point(1,0), new Point(1,1), new Point(2,1) }
	}, 0);
	
	public static final Piece [] ALL_PIECES = new Piece [] {J_PIECE, L_PIECE, LINE_PIECE, BLOCK_PIECE, RIGHT_PIECE, LEFT_PIECE, T_PIECE};
	
	public PieceSensor (Color [][] board) {
		this.board = board;
	}
	
	public Piece getCurrentPiece() {
		if (board == null) {
			return null;
		}
		
		ArrayList<Point> points = new ArrayList<Point>();
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
                if (board[r][c].equals(DEST_COLOR)) {
					points.add(new Point(r, c));
				}
			}
		}
		
		if (points.size() < 4) {
			//overlap or something else lame
			return null;
		}
		
		int minR = board.length;
		int minC = board[0].length;
		
		for (Point p : points) {
			if (p.x < minR) {
				minR = p.x;
			}
			if (p.y < minC) {
				minC = p.y;
			}
		}
		
		for (Point p : points) {
			p.x = p.x - minR;
			p.y = p.y - minC;
		}
		
		Point [] pointArray = new Point [points.size()];
		int index = 0;
		for (Point p : points) {
			pointArray[index++] = p;
		}
	
		
		Piece dropping = new Piece(new Point(minR, minC), new Point [][] {pointArray}, 0);
		for (Piece piece : ALL_PIECES) {
			int result = piece.getIndexOf(dropping);
			if (result != -1) {
				return new Piece(new Point(minR, minC), piece, result);
			}
		}
		return null;
	}
	
	public static boolean isPieceColor(Color c) {
		return c.equals(J_PIECE_COLOR) || c.equals(L_PIECE_COLOR) || c.equals(BLOCK_PIECE_COLOR) || c.equals(LINE_PIECE_COLOR ) 
				|| c.equals(RIGHT_PIECE_COLOR) || c.equals(LEFT_PIECE_COLOR) || c.equals(T_PIECE_COLOR);
		
	}
}
