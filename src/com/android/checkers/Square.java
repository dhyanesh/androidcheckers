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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((piece == null) ? 0 : piece.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Square other = (Square) obj;
		if (piece == null) {
			if (other.piece != null)
				return false;
		} else if (!piece.equals(other.piece))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
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
