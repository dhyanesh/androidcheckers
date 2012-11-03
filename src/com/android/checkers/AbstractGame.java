package com.android.checkers;

import java.io.Serializable;
import java.util.HashSet;

public abstract class AbstractGame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractGame() {
		gameCore = new GameCore();
		moveSquares = new HashSet<Square>();
		jumpSquares = new HashSet<Square>();
	}

	protected GameCore gameCore;
	protected Square selectedSquare;
	protected HashSet<Square> moveSquares;
	protected HashSet<Square> jumpSquares;

	public abstract void doMove(int x, int y);

	protected void doPlayerMove(int x, int y) {
		if (x < 0 || y < 0 || x >= gameCore.getBoard().size() || y >= gameCore.getBoard().size()) {
			return;
		}

		Square currentSquare = gameCore.getBoard().getSquare(x, y);

		// If we need to move again, we can't de-select the square.
		if (currentSquare == selectedSquare && !gameCore.isMoveAgainMode()) {
			deselectSquare();
			clearMoveSquares();
			return;
		}

		if (selectedSquare != null) {
			boolean hasMoved = gameCore.doMove(selectedSquare, currentSquare);
			if (gameCore.isMoveAgainMode()) {
				// If we need to move again, we start from the current square.
				if (hasMoved) {
					selectSquare(currentSquare);
					computeMoveSquares();
				}
			} else {
				deselectSquare();
				clearMoveSquares();
			}
		} else if (gameCore.getMovePieces().contains(currentSquare)) {
			selectSquare(currentSquare);
			computeMoveSquares();
		}
	}

	public Board getBoard() {
		return gameCore.getBoard();
	}

	public boolean canUndo() {
		return gameCore.canUndo();
	}

	public boolean isHighlightedSquare(Square square) {
		return selectedSquare == square || moveSquares.contains(square)
				|| jumpSquares.contains(square);
	}

	protected void selectSquare(Square square) {
		deselectSquare();
		selectedSquare = square;
	}

	protected void deselectSquare() {
		if (selectedSquare == null) {
			return;
		}
		selectedSquare = null;
	}

	protected void clearMoveSquares() {
		moveSquares.clear();
		jumpSquares.clear();
	}

	protected void computeMoveSquares() {
		clearMoveSquares();
		gameCore.maybeAddValidMoveSquares(selectedSquare, moveSquares);
		gameCore.maybeAddValidJumpSquares(selectedSquare, jumpSquares);
	}

	public void undoMove() {
		gameCore.undoMove();
		deselectSquare();
		clearMoveSquares();
	}
}