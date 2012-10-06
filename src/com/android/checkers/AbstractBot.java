/**
 * 
 */
package com.android.checkers;

/**
 * @author dhyanesh
 *
 */
public abstract class AbstractBot {
	protected GameCore gameCore;
	
	public AbstractBot(GameCore gameCore) {
		this.gameCore = gameCore;
	}
	
	// Should return true if there was a valid  move made.
	public abstract boolean playBotMove();
}
