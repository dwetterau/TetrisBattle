package com.david.tetris;

import java.awt.Point;
import java.util.Arrays;

public class Piece {
	
	private Point root;
	
	private Point [][] blocks;
	
	private int index;
	
	public int height;
	
	public Piece(Point p, Point [][] blocks, int index) {
		this.root = p;
		this.blocks = blocks;
		this.index= index;
	}
	
	public Piece(Point p, Piece piece, int index) {
		this.root = p;
		this.index = index;
		blocks = piece.getBlocks();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Piece) {
			Point [][] blocks = ((Piece) o).getBlocks();
			for (int i = 0; i < blocks[((Piece) o).getIndex()].length; i++) {
				if (!blocks[index][i].equals(this.blocks[index][i])) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int getIndexOf(Piece p) {
		for (int i = 0; i < blocks.length; i++) {
			boolean good = true;
			for (int b = 0; b < blocks[i].length; b++) {
				if (!blocks[i][b].equals(p.getBlocks()[p.getIndex()][b])) {
					good = false;
				}
			}
			if (good) {
				return i;
			}
		}
		return -1;
	}
	

	public Point getRoot() {
		return root;
	}
	
	public Point [][] getBlocks() {
		return blocks;
	}
	
	public int getIndex() { 
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String toString() {
		String toReturn = "Piece index: "+index+", Root: " + root +", blocks: {";
		for (int i = 0; i < blocks.length; i++) {
			toReturn += Arrays.toString(blocks[i]);
		}
		toReturn += "}";
		return toReturn;		
	}
}
