package com.android.checkers;

import java.util.HashSet;
import java.util.Random;

public class OnePlayerGame extends AbstractGame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Player player;
	private Random randomNumGenerator;
	
	public OnePlayerGame(Player player) {
		super();
		this.player = player;
		randomNumGenerator = new Random();
		
		if (player == Player.BLACK) {
			playComputerMove();
		}
	}
	
	private Square pickRandomSquare(HashSet<Square> squareSet) {
		int index = randomNumGenerator.nextInt(squareSet.size());
		int i = 0;
		
		for(Square square : squareSet)
		{
		    if (i == index)
		        return square;
		    i++;
		}
		
		return null;
	}

	private void playComputerMove() {
		HashSet<Square> pieces = gameCore.getMovePieces();
		Square srcSquare = pickRandomSquare(pieces);
		HashSet<Square> possibleMoves = new HashSet<Square>();
		gameCore.maybeAddValidMoveSquares(srcSquare, possibleMoves);
		gameCore.maybeAddValidJumpSquares(srcSquare, possibleMoves);
		Square destSquare = pickRandomSquare(possibleMoves);
		gameCore.doMove(srcSquare, destSquare);
	}

	public void doMove(int x, int y) {
		doPlayerMove(x, y);
		while (player != gameCore.getCurrentPlayer()) {
			playComputerMove();
		}
	}
}
