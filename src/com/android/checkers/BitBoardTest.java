/**
 * 
 */
package com.android.checkers;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dhyanesh
 *
 */
public class BitBoardTest {

	/**
	 * Test method for {@link com.android.checkers.BitBoard#BitBoard(com.android.checkers.Board)}.
	 */
	@Test
	public void testBitBoardBoard() {
		Board board = new Board();
		BitBoard bitBoard = new BitBoard(board);
		assertEquals(0xfff, bitBoard.getWhitePieces());
		assertEquals(0xfff00000, bitBoard.getBlackPieces());
	}

	/**
	 * Test method for {@link com.android.checkers.BitBoard#UpdateBoard(com.android.checkers.Board)}.
	 */
	@Test
	public void testUpdateBoard() {
		BitBoard bitBoard = new BitBoard();
		bitBoard.setWhitePieces(0xfff);
		bitBoard.setBlackPieces(0xfff00000);
		
		Board board = new Board();
		bitBoard.UpdateBoard(board);
		assertTrue(board.equals(new Board()));
	}

}
