package com.android.checkers;

import java.util.HashSet;
import java.util.Random;

/**
 * @author dhyanesh
 * 
 * TODO: Need to properly handle undoMove i.e. undo should go back until it is the player's move.
 *
 */
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

		for (Square square : squareSet) {
			if (i == index)
				return square;
			i++;
		}

		return null;
	}

	private boolean playComputerMove() {
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

	@Override
	public void doMove(int x, int y) {
		doPlayerMove(x, y);
		while (player != gameCore.getCurrentPlayer()) {
			if (!playComputerMove()) {
				// There are no more valid moves that the computer can make.
				break;
			}
		}
	}
}
