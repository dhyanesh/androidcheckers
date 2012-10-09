package com.android.checkers;

import java.io.Serializable;
import java.util.Arrays;

public class Board implements Serializable {
	/**
   * 
   */
	private static final long serialVersionUID = 1L;

	private final static int kSize = 8;

	private Square board[][];

	public Board() {
		board = new Square[kSize][kSize];
		for (int i = 0; i < kSize; ++i) {
			for (int j = 0; j < kSize; ++j) {
				board[i][j] = new Square(i, j);
				if (isBlackSquare(i, j) && (j <= 2 || j >= 5)) {
					if (j >= 5) {
						board[i][j].setPiece(Piece.getBlackPiece());
					} else {
						board[i][j].setPiece(Piece.getWhitePiece());
					}
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(board);
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
		Board other = (Board) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		return true;
	}



	public Square getSquare(int x, int y) {
		return board[x][y];
	}

	public int size() {
		return kSize;
	}

	public boolean isValidSquare(int x, int y) {
		return x >= 0 && y >= 0 && x < size() && y < size();
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isBlackSquare(int x, int y) {
		return !isWhiteSquare(x, y);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isWhiteSquare(int x, int y) {
		return (x + y) % 2 != 0;
	}
}
