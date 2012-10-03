package com.android.checkers;

import java.io.Serializable;

public class Square implements Serializable {
	/**
   * 
   */
	private static final long serialVersionUID = 1L;
	private Piece piece;
	private int x;
	private int y;

	public Square(int x, int y) {
		this.piece = null;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Piece getPiece() {
		return piece;
	}

	public boolean hasPlayer(Player player) {
		return !isEmptySquare() && this.piece.getPlayer() == player;
	}

	public boolean isEmptySquare() {
		return piece == null;
	}

	public void setEmptySquare() {
		piece = null;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}
}
