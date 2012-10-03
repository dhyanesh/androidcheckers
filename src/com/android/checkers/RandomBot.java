/**
 * 
 */
package com.android.checkers;

import java.util.HashSet;
import java.util.Random;

/**
 * @author dhyanesh
 *
 */
public class RandomBot extends AbstractBot {
	private Random randomNumGenerator;
	
	/**
	 * @param gameCore
	 */
	public RandomBot(GameCore gameCore) {
		super(gameCore);
		randomNumGenerator = new Random();
	}
	
	private Square pickRandomSquare(HashSet<Square> squareSet) {
		int index = randomNumGenerator.nextInt(squareSet.size());
		int i = 0;

		for (Square square : squareSet) {
			if (i == index)
				return square;
			i++;
		}

		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.android.checkers.AbstractBot#playBotMove(com.android.checkers.GameCore)
	 */
	@Override
	public boolean playBotMove() {
		HashSet<Square> pieces = gameCore.getMovePieces();
		// There are no pieces that can be moved.
		if (pieces.size() == 0) {
			return false;
		}
		Square srcSquare = pickRandomSquare(pieces);
		HashSet<Square> possibleMoves = new HashSet<Square>();
		gameCore.maybeAddValidMoveSquares(srcSquare, possibleMoves);
		gameCore.maybeAddValidJumpSquares(srcSquare, possibleMoves);
		Square destSquare = pickRandomSquare(possibleMoves);
		if (possibleMoves.size() == 0) {
			return false;
		}
		gameCore.doMove(srcSquare, destSquare);
		return true;
	}

}
