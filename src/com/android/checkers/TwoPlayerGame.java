package com.android.checkers;

public class TwoPlayerGame extends AbstractGame {
	/* (non-Javadoc)
	 * @see com.android.checkers.GameInterface#doMove(int, int)
	 */
	private static final long serialVersionUID = 1L;

	public void doMove(int x, int y) {
		doPlayerMove(x, y);
	}
}
