package com.android.checkers;

import java.util.HashSet;

import com.android.checkers.Piece.Player;

public class Game {
	private Board board;
	private Square selectedSquare;
	private HashSet<Square> moveSquares;
	private HashSet<Square> jumpSquares;
	private HashSet<Square> movePieces;
	private Player currentPlayer;
	
	public Game() {
		board = new Board();
		moveSquares = new HashSet<Square>();
		jumpSquares = new HashSet<Square>();
		movePieces = new HashSet<Square>();
		currentPlayer = Player.WHITE;
		recomputeValidSquares();
	}
	
	public Board getBoard() {
		return board;
	}
	
	private void selectSquare(Square square) {
		deselectSquare();
		selectedSquare = square;
		selectedSquare.setHighlighted(true);
	}
	
	private void deselectSquare() {
		if (selectedSquare == null) {
			return;
		}
		selectedSquare.setHighlighted(false);
		selectedSquare = null;
	}
	
	private void highlightSquareSet(HashSet<Square> squares) {
		for (Square square: squares) {
			square.setHighlighted(true);
		}
	}
	
	private void highlightValidSquares() {
		highlightSquareSet(moveSquares);
		highlightSquareSet(jumpSquares);
	}
	
	private void clearSquareSet(HashSet<Square> squares) {
		for (Square square: squares) {
			square.setHighlighted(false);
		}
		squares.clear();
	}
	
	private void clearValidSquares() {
		clearSquareSet(moveSquares);
		clearSquareSet(jumpSquares);
		clearSquareSet(movePieces);
	}
	
	private boolean maybeAddSquareToSet(int x, int y, HashSet<Square> squares) {
		if (board.isValidSquare(x, y) && board.getSquare(x, y).isEmptySquare()) {
			if (squares != null) squares.add(board.getSquare(x, y));
			return true;
		}
		
		return false;
	}
	
	private int getYDirection(Square square) {
		return square.getPiece().isBlack() ? 1 : -1;
	}
	
	private boolean maybeAddValidJumpSquares(int x, int y, int xDiff, int yDiff,
			Player oppositePlayer, HashSet<Square> squares) {
		int adjacentX = x + xDiff;
		int adjacentY = y + yDiff;
		int jumpX = x + 2 * xDiff;
		int jumpY = y + 2 * yDiff;
		
		if (board.isValidSquare(adjacentX, adjacentY) &&
				board.getSquare(adjacentX, adjacentY).hasPlayer(oppositePlayer)) {
			return maybeAddSquareToSet(jumpX, jumpY, squares);
		}
		
		return false;
	}
	
	private boolean maybeAddValidJumpSquares(Square origin, HashSet<Square> squares) {
		if (origin == null) {
			return false;
		}
		
		int x = origin.getX();
		int y = origin.getY();
		int yDiff = getYDirection(origin);
		Player oppositePlayer = Player.getOppositePlayer(origin.getPiece().getPlayer());
		
		boolean value = maybeAddValidJumpSquares(x, y, 1, yDiff, oppositePlayer, squares);
		value |= maybeAddValidJumpSquares(x, y, -1, yDiff, oppositePlayer, squares);
		return value;
	}
	
	private boolean maybeAddValidMoveSquares(Square origin, HashSet<Square> squares) {
		if (origin == null) {
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
		if (selectedSquare != null) {
			return;
		}
		
		for (int i = 0; i < board.size(); ++i) {
			for (int j = 0; j < board.size(); ++j) {
				Square square = board.getSquare(i, j);
				if (square.isEmptySquare() || currentPlayer != square.getPiece().getPlayer()) {
					continue;
				}
					
				if (maybeAddValidMoveSquares(square, null) || maybeAddValidJumpSquares(square, null)) {
					movePieces.add(square);
				}
			}
		}
	}
	
	private void recomputeValidSquares() {
		clearValidSquares();
		
		maybeAddValidMovePieces();
		maybeAddValidMoveSquares(selectedSquare, moveSquares);
		maybeAddValidJumpSquares(selectedSquare, jumpSquares);
		
		highlightValidSquares();
	}
	
	private void switchPlayer() {
		currentPlayer = Player.getOppositePlayer(currentPlayer);
	}
	
	public void doMove(int x, int y) {
		Square currentSquare = board.getSquare(x, y);
		
		if (currentSquare == selectedSquare) {
			deselectSquare();
		} else if (currentSquare.isEmptySquare()) {
			if (selectedSquare != null) {
				if (moveSquares.contains(currentSquare)) {
					currentSquare.setPiece(selectedSquare.getPiece());
					selectedSquare.setEmptySquare();
					switchPlayer();
				} else if (jumpSquares.contains(currentSquare)) {
					boolean killed = killPiece(selectedSquare, currentSquare);
					currentSquare.setPiece(selectedSquare.getPiece());
					selectedSquare.setEmptySquare();
					if (killed) {
						selectSquare(currentSquare);
					} else {
						switchPlayer();
					}
				}
			}
			deselectSquare();
		} else if (movePieces.contains(currentSquare)) {
			selectSquare(currentSquare);
		}
		
		recomputeValidSquares();
	}

	private boolean killPiece(Square startSquare, Square endSquare) {
		int xDiff = endSquare.getX() - startSquare.getX() > 0 ? 1 : -1;
		int yDiff = endSquare.getY() - startSquare.getY() > 0 ? 1 : -1;
		
		boolean killed = false;
		int x = startSquare.getX() + xDiff;
		int y = startSquare.getY() + yDiff;
		for (; x != endSquare.getX(); x += xDiff, y += yDiff) {
			Square square = board.getSquare(x, y);
			if (!square.isEmptySquare()) {
				square.setEmptySquare();
				killed = true;
			}
		}
		
		return killed;
	}
}
