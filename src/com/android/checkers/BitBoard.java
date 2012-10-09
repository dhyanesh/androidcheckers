/**
 * 
 */
package com.android.checkers;

/**
 * @author dhyanesh
 *
 * BitBoard stores the board as bits in two integers.
 * 
 *	# 28 # 29 # 30 # 31
		24 # 25 # 26 # 27 #
		# 20 # 21 # 22 # 23
		16 # 17 # 18 # 19 #
		# 12 # 13 # 14 # 15
		08 # 09 # 10 # 11 #
		# 04 # 05 # 06 # 07
		00 # 01 # 02 # 03 #
 */

public class BitBoard {
	// A set bit in whitePieces indicates that there is a white piece at that bit
	// position as shown in the diagram above.
	private int whitePieces;
	// A set bit in blackPieces indicates that there is a black piece at that bit
	// position as shown in the diagram above.
	private int blackPieces;

	public BitBoard(Board board) {
		whitePieces = 0;
		blackPieces = 0;
		
		for (int i = 0; i < board.size(); ++i) {
			for (int j = 0; j < board.size(); ++j) {
				Square square = board.getSquare(i, j);
				if (square.isEmptySquare()) continue;
				
				if (square.getPiece().isWhite()) {
					setWhitePieceAtIndex(i, j);
				} else {
					setBlackPieceAtIndex(i, j);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "BitBoard [whitePieces=" + Integer.toHexString(whitePieces) + ", blackPieces="
				+ Integer.toHexString(blackPieces) + "]";
	}

	public BitBoard(int whitePieces, int blackPieces) {
		this.whitePieces = whitePieces;
		this.blackPieces = blackPieces;
	}
	
	public BitBoard() {
	}

	// TODO(dhyanesh): Replace all code with BitBoard to avoid to have to do this.
	public void UpdateBoard(Board board) {
		for (int i = 0; i < board.size(); ++i) {
			for (int j = 0; j < board.size(); ++j) {
				Square square = board.getSquare(i, j);
				if (isWhitePieceAtIndex(i, j)) {
					square.setPiece(Piece.getWhitePiece());
				} else if (isBlackPieceAtIndex(i, j)) {
					square.setPiece(Piece.getBlackPiece());
				} else {
					square.setEmptySquare();
				}
			}
		}
	}
	
	public int getWhitePieces() {
		return whitePieces;
	}
	public void setWhitePieces(int whitePieces) {
		this.whitePieces = whitePieces;
	}
	public int getBlackPieces() {
		return blackPieces;
	}
	public void setBlackPieces(int blackPieces) {
		this.blackPieces = blackPieces;
	}
	
	private boolean isWhitePieceAtIndex(int x, int y) {
		return Board.isBlackSquare(x, y) && (whitePieces & getBitMaskForSquare(x, y)) != 0;
	}
	
	private boolean isBlackPieceAtIndex(int x, int y) {
		return Board.isBlackSquare(x, y) && (blackPieces & getBitMaskForSquare(x, y)) != 0;
		
	}
	
	private void setWhitePieceAtIndex(int x, int y) {
		whitePieces |= getBitMaskForSquare(x, y);
	}
	
	private void setBlackPieceAtIndex(int x, int y) {
		blackPieces |= getBitMaskForSquare(x, y);
	}
	
	private int getBitMaskForSquare(int x, int y) {
		return (1 << getBitIndexForSquare(x, y));
	}
	
	private int getBitIndexForSquare(int x, int y) {
		return y * 4 + x / 2;
	}
}
