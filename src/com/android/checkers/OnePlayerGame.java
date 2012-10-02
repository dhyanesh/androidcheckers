package com.android.checkers;

public class OnePlayerGame implements GameInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TwoPlayerGame twoPlayerGame;
	private Player player;
	
	public OnePlayerGame(Player player) {
		twoPlayerGame = new TwoPlayerGame();
		this.player = player;
		
		if (player == Player.BLACK) {
			playComputerMove();
		}
	}

	private void playComputerMove() {
	}

	public Board getBoard() {
		return twoPlayerGame.getBoard();
	}

	public boolean canUndo() {
		return twoPlayerGame.canUndo();
	}

	public boolean isHighlightedSquare(Square square) {
		return twoPlayerGame.isHighlightedSquare(square);
	}

	public void doMove(int x, int y) {
		twoPlayerGame.doMove(x, y);
	}

	public void undoMove() {
		twoPlayerGame.undoMove();
	}
}
