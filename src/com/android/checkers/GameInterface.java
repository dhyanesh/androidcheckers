package com.android.checkers;

import java.io.Serializable;

public interface GameInterface extends Serializable {

	public abstract Board getBoard();

	public abstract boolean canUndo();

	public abstract boolean isHighlightedSquare(Square square);

	public abstract void doMove(int x, int y);

	public abstract void undoMove();

}