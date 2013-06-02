package com.android.checkers;

import java.util.HashSet;
import java.util.Stack;

import java.io.Serializable;

public class GameCore implements Serializable {
	/**
   * 
   */
	private static final long serialVersionUID = 1L;

	private class Move {
		public Move(Square srcSquare, Square destSquare, Square killSquare) {
			super();
			this.srcSquare = srcSquare;
			this.destSquare = destSquare;
			this.killSquare = killSquare;
		}

		// Indicates whether the player changed in this move.
		// The player may not change when there are multiple jumps.
		private boolean switchPlayer;
		// Indicates whether moveAgainMode was flipped in this move.
		private boolean flipMoveAgainMode;
		// Store the source, destination and killed piece of this move.
		private Square srcSquare;
		private Square destSquare;
		private Square killSquare;

		public Square getKillSquare() {
			return killSquare;
		}

		public Square getSrcSquare() {
			return srcSquare;
		}

		public Square getDestSquare() {
			return destSquare;
		}

		public boolean shouldSwitchPlayer() {
			return switchPlayer;
		}

		public void setSwitchPlayer(boolean switchPlayer) {
			this.switchPlayer = switchPlayer;
		}

		public boolean shouldFlipMoveAgainMode() {
			return flipMoveAgainMode;
		}

		public void setFlipMoveAgainMode(boolean flipMoveAgainMode) {
			this.flipMoveAgainMode = flipMoveAgainMode;
		}
	}

	private Board board;
	private HashSet<Square> movePieces;
	private Player currentPlayer;
	// Indicates that we are in moveAgainMode.
	private boolean moveAgainMode;

	// Save the entire undo stack.
	private Stack<Move> moveStack;

	public GameCore() {
		board = new Board();
		movePieces = new HashSet<Square>();
		moveStack = new Stack<Move>();
		currentPlayer = Player.WHITE;
		moveAgainMode = false;
		computeValidMovePieces();
	}

	public HashSet<Square> getMovePieces() {
		return movePieces;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Board getBoard() {
		return board;
	}

	public boolean canUndo() {
		return !moveStack.empty();
	}

	public boolean isMoveAgainMode() {
		return moveAgainMode;
	}

	public void setMoveAgainMode(boolean moveAgainMode) {
		this.moveAgainMode = moveAgainMode;
	}

	private void clearValidMovePieces() {
		movePieces.clear();
	}

	private boolean maybeAddSquareToSet(int x, int y, HashSet<Square> squares) {
		if (board.isValidSquare(x, y) && board.getSquare(x, y).isEmptySquare()) {
			if (squares != null)
				squares.add(board.getSquare(x, y));
			return true;
		}

		return false;
	}

	private int getYDirection(Square square) {
		return square.getPiece().isBlack() ? -1 : 1;
	}

	private boolean maybeAddValidJumpSquares(int x, int y, int xDiff, int yDiff,
			Player oppositePlayer, HashSet<Square> squares) {
		int adjacentX = x + xDiff;
		int adjacentY = y + yDiff;
		int jumpX = x + 2 * xDiff;
		int jumpY = y + 2 * yDiff;

		if (board.isValidSquare(adjacentX, adjacentY)
				&& board.getSquare(adjacentX, adjacentY).hasPlayer(oppositePlayer)) {
			return maybeAddSquareToSet(jumpX, jumpY, squares);
		}

		return false;
	}

	public boolean maybeAddValidJumpSquares(Square origin, HashSet<Square> squares) {
		if (origin == null) {
			return false;
		}

		int x = origin.getX();
		int y = origin.getY();
		int yDiff = getYDirection(origin);
		Player oppositePlayer = Player.getOppositePlayer(origin.getPiece().getPlayer());

		int xDiff = 1;
		boolean value = maybeAddValidJumpSquares(x, y, xDiff, yDiff, oppositePlayer, squares);
		xDiff = -1;
		value |= maybeAddValidJumpSquares(x, y, xDiff, yDiff, oppositePlayer, squares);
		return value;
	}

	public boolean maybeAddValidMoveSquares(Square origin, HashSet<Square> squares) {
		if (origin == null || isMoveAgainMode()) {
			return false;
		}

		int x = origin.getX();
		int y = origin.getY();
		int yDiff = getYDirection(origin);

		boolean value = maybeAddSquareToSet(x + 1, y + yDiff, squares);
		value |= maybeAddSquareToSet(x - 1, y + yDiff, squares);
		return value;
	}

	private void maybeAddValidMovePieces() {
		for (int i = 0; i < board.size(); ++i) {
			for (int j = 0; j < board.size(); ++j) {
				Square square = board.getSquare(i, j);
				if (square.isEmptySquare()
						|| currentPlayer != square.getPiece().getPlayer()) {
					continue;
				}

				if (maybeAddValidMoveSquares(square, null)
						|| maybeAddValidJumpSquares(square, null)) {
					// The square is a valid move square only if the piece on it can move
					// or jump.
					movePieces.add(square);
				}
			}
		}
	}

	public void computeValidMovePieces() {
		clearValidMovePieces();
		maybeAddValidMovePieces();
	}

	public void switchPlayer() {
		currentPlayer = Player.getOppositePlayer(currentPlayer);
	}

	private boolean maybeMovePiece(Square srcSquare, Square destSquare) {
		HashSet<Square> moveSquares = new HashSet<Square>();
		maybeAddValidMoveSquares(srcSquare, moveSquares);

		if (!moveSquares.contains(destSquare)) {
			return false;
		}

		// First save the undo state.
		moveStack.push(new Move(srcSquare, destSquare, null));
		moveStack.peek().setSwitchPlayer(true);

		// Move the current player's piece.
		destSquare.setPiece(srcSquare.getPiece());
		srcSquare.setEmptySquare();
		switchPlayer();
		computeValidMovePieces();
		return true;
	}

	private boolean maybeKillPiece(Square srcSquare, Square destSquare) {
		HashSet<Square> jumpSquares = new HashSet<Square>();
		maybeAddValidJumpSquares(srcSquare, jumpSquares);
		if (!jumpSquares.contains(destSquare)) {
			return false;
		}

		// Kill the piece in between.
		Square killSquare = killPiece(srcSquare, destSquare);
		assert killSquare != null;

		// First save the move.
		moveStack.push(new Move(srcSquare, destSquare, killSquare));

		// Move the current player's piece.
		destSquare.setPiece(srcSquare.getPiece());
		srcSquare.setEmptySquare();

		// If there are more jump squares for this piece, the current player still
		// plays, otherwise we switch players.
		if (maybeAddValidJumpSquares(destSquare, jumpSquares)) {
			moveStack.peek().setSwitchPlayer(false);
			// We add the square to movePieces since this is the only valid move that
			// is available until we are out of moveAgainMode.
			if (!isMoveAgainMode()) {
				moveAgainMode = true;
				// We can kill more so set moveAgainMode to true.
				moveStack.peek().setFlipMoveAgainMode(true);
			}
			clearValidMovePieces();
			movePieces.add(destSquare);
		} else {
			moveStack.peek().setSwitchPlayer(true);
			if (isMoveAgainMode()) {
				// We can't kill more so set moveAgainMode to false.
				moveAgainMode = false;
				moveStack.peek().setFlipMoveAgainMode(true);
			}
			switchPlayer();
			computeValidMovePieces();
		}

		return true;
	}

	public boolean doMove(Square srcSquare, Square destSquare) {
		if (!destSquare.isEmptySquare()) {
			return false;
		}

		// If we are in moveAgainMode mode, we can only kill pieces. Moves are not
		// allowed.
		if (isMoveAgainMode()) {
			return maybeKillPiece(srcSquare, destSquare);
		}

		if (maybeMovePiece(srcSquare, destSquare)) {
			return true;
		} else {
			return maybeKillPiece(srcSquare, destSquare);
		}
	}

	private Square killPiece(Square startSquare, Square endSquare) {
		int xDiff = endSquare.getX() - startSquare.getX() > 0 ? 1 : -1;
		int yDiff = endSquare.getY() - startSquare.getY() > 0 ? 1 : -1;

		Square killSquare = null;
		int x = startSquare.getX() + xDiff;
		int y = startSquare.getY() + yDiff;
		for (; x != endSquare.getX(); x += xDiff, y += yDiff) {
			Square square = board.getSquare(x, y);
			if (!square.isEmptySquare()) {
				killSquare = square;
				square.setEmptySquare();
			}
		}

		return killSquare;
	}

	public void undoMove() {
		if (moveStack.empty()) {
			return;
		}

		Move lastMove = moveStack.pop();
		Piece movedPiece = lastMove.getDestSquare().getPiece();
		assert movedPiece != null;
		lastMove.getSrcSquare().setPiece(movedPiece);
		lastMove.getDestSquare().setEmptySquare();

		Square killSquare = lastMove.getKillSquare();
		if (killSquare != null) {
			killSquare.setPiece(movedPiece.isWhite() ? Piece.getBlackPiece() : Piece
					.getWhitePiece());
		}

		if (lastMove.shouldSwitchPlayer()) {
			switchPlayer();
		}

		if (lastMove.shouldFlipMoveAgainMode()) {
			moveAgainMode = !moveAgainMode;
		}

		computeValidMovePieces();
	}
}