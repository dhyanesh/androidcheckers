/**
 * 
 */
package com.android.checkers;

import android.util.Log;

/**
 * @author dhyanesh
 *
 */
public class NativeBot extends AbstractBot {
	protected MoveResult moveResult;
	
	public class MoveResult {
		@Override
		public String toString() {
			return "MoveResult [whitePieces=" + whitePieces + ", blackPieces="
					+ blackPieces + ", isMoveAgainMode=" + isMoveAgainMode + "]";
		}
		public int whitePieces;
		public int blackPieces;
		public boolean isMoveAgainMode;
	}

	public NativeBot(GameCore gameCore) {
		super(gameCore);
	}
	
	public boolean playBotMove() {
		BitBoard bitBoard = new BitBoard(gameCore.getBoard());
		Log.i("NativeBot", "Before: " + bitBoard.toString());
		moveResult = new MoveResult();
		if (!playNativeBotMove(bitBoard.getWhitePieces(), bitBoard.getBlackPieces(),
				gameCore.getCurrentPlayer() == Player.WHITE, gameCore.isMoveAgainMode())) {
			return false;
		}

		bitBoard.setWhitePieces(moveResult.whitePieces);
		bitBoard.setBlackPieces(moveResult.blackPieces);
		bitBoard.UpdateBoard(gameCore.getBoard());
		gameCore.setMoveAgainMode(moveResult.isMoveAgainMode);
		if (!moveResult.isMoveAgainMode) {
			gameCore.switchPlayer();
		}
		gameCore.computeValidMovePieces();
		Log.i("NativeBot", "After: " + moveResult.toString());
		return true;
	}
	
	public native boolean playNativeBotMove(int whitePieces, int blackPieces,
			boolean isWhitePlayer, boolean isMoveAgainMode);
	
  static {
    System.loadLibrary("gnustl_shared");
    System.loadLibrary("native_bot");
  }
}
