/**
 * 
 */
package com.android.checkers;

import static org.junit.Assert.*;
import junit.framework.Assert;

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
		assertEquals(0, bitBoard.getWhitePieces());
		assertEquals(0, bitBoard.getBlackPieces());
		
		bitBoard.UpdateBoard(board);
		assertEquals(board, new Board());
	}

	/**
	 * Test method for {@link com.android.checkers.BitBoard#UpdateBoard(com.android.checkers.Board)}.
	 */
	@Test
	public void testUpdateBoard() {
		
	}

}
