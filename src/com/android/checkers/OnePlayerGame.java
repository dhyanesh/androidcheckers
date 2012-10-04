package com.android.checkers;

/**
 * @author dhyanesh
 * 
 * TODO: Need to properly handle undoMove i.e. undo should go back until it is the player's move.
 *
 */
public class OnePlayerGame extends AbstractGame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Player player;
	private AbstractBot bot;

	public OnePlayerGame(Player player) {
		super();
		this.player = player;
		bot = new RandomBot(gameCore);

		if (player == Player.BLACK) {
			bot.playBotMove();
		}
	}

	@Override
	public void doMove(int x, int y) {
		doPlayerMove(x, y);
		while (player != gameCore.getCurrentPlayer()) {
			if (!bot.playBotMove()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// There are no more valid moves that the bot can make.
				break;
			}
		}
	}
}
