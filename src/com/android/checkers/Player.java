package com.android.checkers;

public enum Player {
	WHITE, BLACK;
	
	public static Player getOppositePlayer(Player player) {
		return player == WHITE ? BLACK : WHITE;
	}
}