/**
 * 
 */
package com.android.checkers;

import java.io.Serializable;

/**
 * @author dhyanesh
 * 
 */
public class Piece implements Serializable {
	/**
   * 
   */
	private static final long serialVersionUID = 1L;

	private Player player;
	private static Piece whitePiece = new Piece(Player.WHITE);
	private static Piece blackPiece = new Piece(Player.BLACK);

	private Piece(Player player) {
		this.player = player;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
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
		Piece other = (Piece) obj;
		if (player != other.player)
			return false;
		return true;
	}

	public static Piece getWhitePiece() {
		return whitePiece;
	}

	public static Piece getBlackPiece() {
		return blackPiece;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isBlack() {
		return player == Player.BLACK;
	}

	public boolean isWhite() {
		return player == Player.WHITE;
	}

	public void setBlack() {
		player = Player.BLACK;
	}

	public void setWhite() {
		player = Player.WHITE;
	}
}
