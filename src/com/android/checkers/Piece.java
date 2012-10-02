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
