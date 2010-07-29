package com.android.checkers;

import java.io.Serializable;

import com.android.checkers.Piece.Player;

public class Square implements Serializable {
	/**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Piece piece;
	private boolean highlighted;
	private int x;
	private int y;

	public Square(int x, int y) {
		this.piece = null;
		this.highlighted = false;
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

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setEmptySquare() {
		piece = null;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}
}
