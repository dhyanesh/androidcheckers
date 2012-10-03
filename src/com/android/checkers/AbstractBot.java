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
	public abstract boolean playBotMove();
}
