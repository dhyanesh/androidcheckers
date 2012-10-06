/**
 * 
 */
package com.android.checkers;

/**
 * @author dhyanesh
 *
 */
public class NativeRandomBot extends AbstractBot {
	protected MoveResult moveResult;
	
	public class MoveResult {
		public int whitePieces;
		public int blackPieces;
		public boolean isMoveAgainMode;
	}

	public NativeRandomBot(GameCore gameCore) {
		super(gameCore);
	}
	
	public boolean playBotMove() {
		BitBoard bitBoard = new BitBoard(gameCore.getBoard());
		moveResult = new MoveResult();
		 if (!playNativeBotMove(bitBoard.getWhitePieces(), bitBoard.getBlackPieces(),
				 		gameCore.getCurrentPlayer() == Player.WHITE, gameCore.isMoveAgainMode())) {
			 return false;
		 }
		 
		 return true;
	}
	
	public native boolean playNativeBotMove(int whitePieces, int blackPieces,
			boolean isWhitePlayer, boolean isMoveAgainMode);
	
  static {
    System.loadLibrary("gnustl_shared");
    System.loadLibrary("native-random-bot");
  }
}
