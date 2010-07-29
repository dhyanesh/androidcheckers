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

  public enum Player {
		WHITE, BLACK;
		
		public static Player getOppositePlayer(Player player) {
			return player == WHITE ? BLACK : WHITE;
		}
	}

	private Player player;
	private static Piece whitePiece = new Piece(Player.WHITE);
	private static Piece blackPiece = new Piece(Player.BLACK);

	private Piece(Player type) {
		this.player = type;
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
