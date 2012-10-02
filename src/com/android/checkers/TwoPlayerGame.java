package com.android.checkers;

import java.util.HashSet;

public class TwoPlayerGame implements GameInterface {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private GameCore gameCore;
	private Square selectedSquare;
	private HashSet<Square> moveSquares;
	private HashSet<Square> jumpSquares;
	
	public TwoPlayerGame() {
		gameCore = new GameCore();
		moveSquares = new HashSet<Square>();
		jumpSquares = new HashSet<Square>();
	}
	
	public Board getBoard() {
		return gameCore.getBoard();
	}
	
	/* (non-Javadoc)
	 * @see com.android.checkers.GameInterface#canUndo()
	 */
	public boolean canUndo() {
	  return gameCore.canUndo();
	}
	
	/* (non-Javadoc)
	 * @see com.android.checkers.GameInterface#isHighlightedSquare(com.android.checkers.Square)
	 */
	public boolean isHighlightedSquare(Square square) {
		return selectedSquare == square || moveSquares.contains(square) || jumpSquares.contains(square);
	}

	private void selectSquare(Square square) {
		deselectSquare();
		selectedSquare = square;
	}

	private void deselectSquare() {
		if (selectedSquare == null) {
			return;
		}
		selectedSquare = null;
	}
	
	private void clearMoveSquares() {
		moveSquares.clear();
		jumpSquares.clear();
	}
	
	private void computeMoveSquares() {
		clearMoveSquares();
		gameCore.maybeAddValidMoveSquares(selectedSquare, moveSquares);
		gameCore.maybeAddValidJumpSquares(selectedSquare, jumpSquares);
	}
	
	/* (non-Javadoc)
	 * @see com.android.checkers.GameInterface#doMove(int, int)
	 */
	public void doMove(int x, int y) {
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

	/* (non-Javadoc)
	 * @see com.android.checkers.GameInterface#undoMove()
	 */
	public void undoMove() {
		gameCore.undoMove();
		deselectSquare();
		clearMoveSquares();
	}
}
