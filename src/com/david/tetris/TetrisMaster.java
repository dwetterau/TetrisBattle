package com.david.tetris;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class TetrisMaster {

    public static Piece heldPiece;

    public static int pieceNumber = 0;
    public static int lastPieceMoved = -1;
    public static ArrayList<Integer> lastMoves;
    private static int interMoveDelay = 20;
    private static int interMoveTypeDelay = 35;

    public static void main(String ... args) throws AWTException, InterruptedException {
		
		TetrisSensor sensor = new TetrisSensor();
		Point p = sensor.getBoardPosition();
		Point [][] points = sensor.getGridPointArray(p);
		
		int width = 0;
		int height = 0;
		
		for (Point [] array : points ) {
			for (Point point : array) {
				point.x -= p.x;
				point.y -= p.y;
				if (point.x > width) {
					width = point.x;
				}
				if (point.y > height) {
					height = point.y;
				}
			}
		}
		
		
		if (p != null) {
			
            //make sure the game has focus. This should be moved
            Robot r = sensor.getRobot();
            r.mouseMove(p.x + 50, p.y + 100);
            r.waitForIdle();
            r.mousePress(InputEvent.BUTTON1_MASK);
            r.waitForIdle();
            r.mouseRelease(InputEvent.BUTTON1_MASK);
            Rectangle rectangle = new Rectangle(p.x, p.y, width + 1, height + 1);

            while (true) {
				//long start = -System.currentTimeMillis();
                r.waitForIdle();
                sensor = new TetrisSensor(points, rectangle, r);

				//System.out.println("Made sensor in: " + ((start + System.currentTimeMillis()) + "ms"));
				//Get piece and move instructions
				ArrayList<Integer> keyPresses = getKeyPresses(sensor, p, points); 
				if (keyPresses != null)
                System.out.println(keyPresses);
				//start = -System.currentTimeMillis();
				
				doMoves(r, keyPresses);

				if (keyPresses != null) {
					if (keyPresses.contains(KeyEvent.VK_SPACE)) {
                        pieceNumber++;
                    }
				}
				//System.out.println("Moved pieces in: " + ((start + System.currentTimeMillis()) + "ms"));
			}
		} else {
			System.out.println("Didn't find board.");
		}
	}
	
	private static ArrayList<Integer> getKeyPresses(TetrisSensor sensor, Point p, Point [][] points) {
		ArrayList<Integer> moveList = new ArrayList<Integer>();
		
		Color [][] colors = sensor.getColorGrid(points);
		
		PieceSensor pieceSensor = new PieceSensor(colors);
		Piece piece = pieceSensor.getCurrentPiece();
		
		double bestScore = Double.MAX_VALUE;
        double bestHeldScore = Double.MAX_VALUE;
		int bestRotation = -1;
		int bestColumn = -1;

		if (piece != null) {

            if (heldPiece == null) {
                System.out.println("Hold first!");
                heldPiece = piece;
                moveList.add(KeyEvent.VK_SHIFT);
                return moveList;
            }
			//got the current piece. Now we need to move and try all its rotations to see where it should go
			for (int c = 0; c < 10; c++) {
				for (int rotation = 0; rotation < piece.getBlocks().length; rotation++) {
					Piece newPiece = new Piece(new Point(0, c), piece, rotation);
                    BoardCalculator calculator = new BoardCalculator(copyGridFrom(colors));
					
					calculator.cleanBoard();
					if (calculator.addPiece(newPiece)) {
						double score = calculator.scoreBoard();
						//high value scores are bad
                        if (score < bestScore) {
							bestScore = score;
							bestRotation = rotation;
							bestColumn = c;
							//bestBoard = calculator.getBoard();
						}
					}
				}
			}

            for (int c = 0; c < 10; c++) {
                for (int rotation = 0; rotation < heldPiece.getBlocks().length; rotation++) {
                    Piece newPiece = new Piece(new Point(0, c), heldPiece, rotation);
                    BoardCalculator calculator = new BoardCalculator(copyGridFrom(colors));

                    calculator.cleanBoard();
                    if (calculator.addPiece(newPiece)) {
                        double score = calculator.scoreBoard();
                        //high value scores are bad
                        if (score < bestHeldScore) {
                            bestHeldScore = score;
                        }
                    }
                }
            }

            if (bestHeldScore < bestScore) {
                //swap held piece!
                System.out.println("Swap!");
                heldPiece = piece;
                moveList.add(KeyEvent.VK_SHIFT);
                return moveList;
            }

			/*System.out.println("new best placement:");
			for (int r = 0; r < 20; r++) {
			for (int col = 0; col < 10; col++) {
				System.out.print(bestBoard[r][col].getRed()+"-"+bestBoard[r][col].getGreen()+ "-" + bestBoard[r][col].getBlue() +"\t");
			}
			System.out.println();
			}
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@"); */
			//System.out.println("Best score: " + bestScore);
			//System.out.println("Best rotation found: " + bestRotation);
			//System.out.println("Best column found: " + bestColumn);
			
			//proper number of rotations
			int rotations = 0;
			if (bestRotation > piece.getIndex()) { 
				rotations = bestRotation - piece.getIndex();
			} else if (bestRotation <  piece.getIndex()) {
				rotations = piece.getBlocks().length - piece.getIndex() + bestRotation;
			}
			int displacement = bestColumn - piece.getRoot().y;
			
			if (rotations == 0 && displacement == 0) {
				moveList.add(KeyEvent.VK_SPACE);
				return moveList;
			}
			
			while (rotations > 0) {
				rotations --;
				moveList.add(KeyEvent.VK_UP);
			}
			while (displacement > 0) {
				displacement--;
				moveList.add(KeyEvent.VK_RIGHT);
			}
			while (displacement < 0) {
				displacement++;
				moveList.add(KeyEvent.VK_LEFT);
			}

			
			//System.out.println("Got board state in: " + ((start + System.currentTimeMillis()) + "ms"));
			return moveList;
		} else {
			//System.out.println("Unable to find current piece");
		}
		return null;
	}

	public static void printBoard(Color [][] colors) {
		for (int r = 0; r < 20; r++) {
			for (int c = 0; c < 10; c++) {
				System.out.print(colors[r][c].getRed()+"-"+colors[r][c].getGreen()+ "-" + colors[r][c].getBlue() +"\t");
			}
			System.out.println();  
		}
	}
	
	public static void doMoves(Robot robot, ArrayList<Integer> moveList) {
		if (moveList == null) {
			return;
		}

        int tuningAmount = 0;

        if (lastPieceMoved == pieceNumber && !moveList.contains(KeyEvent.VK_SPACE)) {
            //This is the second time trying to move this piece, something is off...
            if (lastMoves != null) {
                tuningAmount = determineTuning(moveList, lastMoves);
            }
        }

        interMoveDelay = Math.max(interMoveDelay + tuningAmount, 10);
        interMoveTypeDelay = Math.max(interMoveDelay + tuningAmount, 10);

		for (int num = 0; num < moveList.size(); num += 1) {
			robot.keyPress(moveList.get(num));
            robot.waitForIdle();
			robot.delay(interMoveDelay);
            robot.waitForIdle();
			robot.keyRelease(moveList.get(num));
            robot.waitForIdle();
            robot.delay(interMoveTypeDelay);
            robot.waitForIdle();
		}

        lastPieceMoved = pieceNumber;
        lastMoves = moveList;
	}

    private static int determineTuning(ArrayList<Integer> moveList, ArrayList<Integer> lastMoves) {
        if (lastPieceMoved == -1 || lastPieceMoved == 0) {
            return 0;
        }
        boolean oldLeft = lastMoves.contains(KeyEvent.VK_LEFT);
        boolean oldRight = lastMoves.contains(KeyEvent.VK_RIGHT);
        boolean left = moveList.contains(KeyEvent.VK_LEFT);
        boolean right = moveList.contains(KeyEvent.VK_RIGHT);

        if ((oldLeft && left) || (oldRight && right)) {
            System.out.println("TUNING: WAITING LONGER");
            return 1;
        } else if ((oldLeft && right) || (oldRight && left)) {
            System.out.println("TUNING: GOING FASTER");
            return -1;
        } else {
            return 0;
        }
    }

    public static Color[][] copyGridFrom(Color [][] grid) {
		Color [][] colors = new Color [grid.length][grid[0].length];
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				colors[r][c] = grid[r][c];
			}
		}
		return colors;
	}
}
