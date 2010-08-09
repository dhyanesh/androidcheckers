/**
 * 
 */
package com.android.checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Dhyanesh Damania
 * 
 */
public class BoardView extends View {

	private int squareSize;
	private int xOffset;
	private int yOffset;
	private Paint blackPaint;
	private Paint whitePaint;
	private Paint whitePiece;
	private Paint blackPiece;
	private Paint highlightPaint;

	private Game game;
	private Board board;

	/**
	 * Constructs a BoardView based on inflation from XML
	 * 
	 * @param context
	 * @param attrs
	 */
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBoardView();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBoardView();
	}

	/**
	 * 
	 */
	private void initBoardView() {
		setBackgroundColor(0xff000000);
		setFocusable(true);
		setFocusableInTouchMode(true);

		blackPaint = new Paint();
		whitePaint = new Paint();
		blackPiece = new Paint();
		whitePiece = new Paint();
		highlightPaint = new Paint();

		blackPaint.setARGB(255, 117, 48, 15);
		blackPaint.setStyle(Paint.Style.FILL);
		whitePaint.setARGB(255, 231, 197, 133);
		whitePaint.setStyle(Paint.Style.FILL);

		blackPiece.setARGB(255, 11, 12, 18);
		blackPiece.setStyle(Paint.Style.FILL);
		whitePiece.setARGB(255, 250, 246, 221);
		whitePiece.setStyle(Paint.Style.FILL);

		highlightPaint.setARGB(255, 0, 0xEE, 0);
		highlightPaint.setStyle(Paint.Style.FILL);

		initGame(new Game());
	}
	
	/**
	 * @param inGame
	 */
	private void initGame(Game inGame) {
	  game = inGame;
		board = game.getBoard();
	}

	/**
	 * @param canvas
	 * @param x
	 * @param y
	 */
	private void drawSquare(Canvas canvas, int x, int y) {
		Paint paint = null;
		if (board.getSquare(x, y).isHighlighted()) {
			paint = highlightPaint;
		} else if (Board.isWhiteSquare(x, y)) {
			paint = whitePaint;
		} else {
			paint = blackPaint;
		}
		int left = xOffset + x * squareSize;
		int top = yOffset + y * squareSize;
		canvas.drawRect(left, top, left + squareSize, top + squareSize, paint);
	}

	/**
	 * @param canvas
	 * @param x
	 * @param y
	 */
	private void maybeDrawPiece(Canvas canvas, int x, int y) {
		Square square = board.getSquare(x, y);
		if (square.isEmptySquare()) {
			return;
		}
		Paint paint = null;
		if (square.getPiece().isBlack()) {
			paint = blackPiece;
		} else {
			paint = whitePiece;
		}
		int cx = xOffset + x * squareSize + squareSize / 2;
		int cy = yOffset + y * squareSize + squareSize / 2;
		canvas.drawCircle(cx, cy, (float) (squareSize / 2.5), paint);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < board.size(); ++i) {
			for (int j = 0; j < board.size(); ++j) {
				drawSquare(canvas, i, j);
				maybeDrawPiece(canvas, i, j);
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		int value = w < h ? w : h;
		squareSize = value / board.size();

		xOffset = (w - squareSize * board.size()) / 2;
		yOffset = (h - squareSize * board.size()) / 2;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}

		int currentX = (int) ((event.getX() - xOffset) / squareSize);
		int currentY = (int) ((event.getY() - yOffset) / squareSize);
		
		game.doMove(currentX, currentY);

		invalidate();
		return true;
	}

  /**
   * @return
   */
  public Bundle saveState() {
    Bundle map = new Bundle();
    map.putSerializable("game", game);
    return map;
  }

  /**
   * @param map
   */
  public void restoreState(Bundle map) {
    initGame((Game) map.getSerializable("game"));
  }

  public void NewGame() {
    initGame(new Game());
  }

  public void undoMove() {
    game.undoMove();
		invalidate();
  }

  public boolean canUndo() {
    return game.canUndo();
  }
}
