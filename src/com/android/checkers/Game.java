package com.android.checkers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Stack;

import com.android.checkers.Piece.Player;

public class Game implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private enum KillState {
		NO_KILL, KILL_ONE, KILL_MORE;
	}
  
  private class Move {
    public Move(Square srcSquare, Square destSquare, Square killSquare) {
      super();
      this.srcSquare = srcSquare;
      this.destSquare = destSquare;
      this.killSquare = killSquare;
    }
    // Indicates whether the player changed in this move.
    // The player may not change when there are multiple jumps.
    private boolean switchPlayer;
    // Indicates whether sticky was flipped in this move.
    private boolean flipSticky;
    // Store the source, destination and killed piece of this move.
    private Square srcSquare;
    private Square destSquare;
    private Square killSquare; 
    
    public Square getKillSquare() {
      return killSquare;
    }

    public Square getSrcSquare() {
      return srcSquare;
    }
    public Square getDestSquare() {
      return destSquare;
    }
    
    public boolean shouldSwitchPlayer() {
      return switchPlayer;
    }
    public void setSwitchPlayer(boolean switchPlayer) {
      this.switchPlayer = switchPlayer;
    }
    public boolean shouldFlipSticky() {
      return flipSticky;
    }
    public void setFlipSticky(boolean flipSticky) {
      this.flipSticky = flipSticky;
    }
  }
	
  private Board board;
	private Square selectedSquare;
	private HashSet<Square> moveSquares;
	private HashSet<Square> jumpSquares;
	private HashSet<Square> movePieces;
	private Player currentPlayer;
	// Indicates that the selected square is sticky.
	private boolean sticky;
	// Save the entire undo stack.
	private Stack<Move> moveStack;
	
	public Game() {
		board = new Board();
		moveSquares = new HashSet<Square>();
		jumpSquares = new HashSet<Square>();
		movePieces = new HashSet<Square>();
		moveStack = new Stack<Move>();
		currentPlayer = Player.WHITE;
		sticky = false;
		recomputeValidSquares();
	}
	
	public Board getBoard() {
		return board;
	}
	
	public boolean canUndo() {
	  return !moveStack.empty();
	}
	
	private void selectSquare(Square square) {
		deselectSquare();
		selectedSquare = square;
		selectedSquare.setHighlighted(true);
	}
	
	private void deselectSquare() {
		if (selectedSquare == null) {
			return;
		}
		selectedSquare.setHighlighted(false);
		selectedSquare = null;
	}
	
	private void highlightSquareSet(HashSet<Square> squares) {
		for (Square square: squares) {
			square.setHighlighted(true);
		}
	}
	
	private void highlightValidSquares() {
		highlightSquareSet(moveSquares);
		highlightSquareSet(jumpSquares);
		if (selectedSquare != null) {
		  selectedSquare.setHighlighted(true);
		}
	}
	
	private void clearSquareSet(HashSet<Square> squares) {
		for (Square square: squares) {
			square.setHighlighted(false);
		}
		squares.clear();
	}
	
	private void clearValidSquares() {
		clearSquareSet(moveSquares);
		clearSquareSet(jumpSquares);
		clearSquareSet(movePieces);
	}
	
	private boolean maybeAddSquareToSet(int x, int y, HashSet<Square> squares) {
		if (board.isValidSquare(x, y) && board.getSquare(x, y).isEmptySquare()) {
			if (squares != null) squares.add(board.getSquare(x, y));
			return true;
		}
		
		return false;
	}
	
	private int getYDirection(Square square) {
		return square.getPiece().isBlack() ? 1 : -1;
	}
	
	private boolean maybeAddValidJumpSquares(int x, int y, int xDiff, int yDiff,
			Player oppositePlayer, HashSet<Square> squares) {
		int adjacentX = x + xDiff;
		int adjacentY = y + yDiff;
		int jumpX = x + 2 * xDiff;
		int jumpY = y + 2 * yDiff;
		
		if (board.isValidSquare(adjacentX, adjacentY) &&
				board.getSquare(adjacentX, adjacentY).hasPlayer(oppositePlayer)) {
			return maybeAddSquareToSet(jumpX, jumpY, squares);
		}
		
		return false;
	}
	
	private boolean maybeAddValidJumpSquares(Square origin, HashSet<Square> squares) {
		if (origin == null) {
			return false;
		}
		
		int x = origin.getX();
		int y = origin.getY();
		int yDiff = getYDirection(origin);
		Player oppositePlayer = Player.getOppositePlayer(origin.getPiece().getPlayer());
		
		int xDiff = 1;
		boolean value = maybeAddValidJumpSquares(x, y, xDiff, yDiff, oppositePlayer, squares);
		xDiff = -1;
		value |= maybeAddValidJumpSquares(x, y, xDiff, yDiff, oppositePlayer, squares);
		return value;
	}
	
	private boolean maybeAddValidMoveSquares(Square origin, HashSet<Square> squares) {
		if (origin == null) {
			return false;
		}
		
		int x = origin.getX();
		int y = origin.getY();
		int yDiff = getYDirection(origin);
		
		boolean value = maybeAddSquareToSet(x + 1, y + yDiff, squares);
		value |= maybeAddSquareToSet(x - 1, y + yDiff, squares);
		return value;
	}
	
	private void maybeAddValidMovePieces() {
		if (selectedSquare != null) {
			return;
		}
		
		for (int i = 0; i < board.size(); ++i) {
			for (int j = 0; j < board.size(); ++j) {
				Square square = board.getSquare(i, j);
				if (square.isEmptySquare() || currentPlayer != square.getPiece().getPlayer()) {
					continue;
				}
					
				if (maybeAddValidMoveSquares(square, null) || maybeAddValidJumpSquares(square, null)) {
				  // The square is a valid move square only if the piece on it can move or jump.
					movePieces.add(square);
				}
			}
		}
	}
	
	private void recomputeValidSquares() {
		clearValidSquares();
		
		maybeAddValidMovePieces();
		maybeAddValidMoveSquares(selectedSquare, moveSquares);
		maybeAddValidJumpSquares(selectedSquare, jumpSquares);
		
		highlightValidSquares();
	}
	
	private void switchPlayer() {
		currentPlayer = Player.getOppositePlayer(currentPlayer);
	}
	
	private KillState maybeKillPiece(Square currentSquare) {
	  if (!jumpSquares.contains(currentSquare)) {
	    return KillState.NO_KILL;
	  }
	  
	  // Kill the piece in between.
	  Square killSquare = killPiece(selectedSquare, currentSquare);
	  assert killSquare != null;
	  
	  // First save the move.
		moveStack.push(new Move(selectedSquare, currentSquare, killSquare));
	  
	  // Move the current player's piece.
	  currentSquare.setPiece(selectedSquare.getPiece());
	  selectedSquare.setEmptySquare();
	  
	  clearValidSquares();
	  // If there are more jump squares for this piece, the current player still plays, otherwise we switch players.
	  boolean kill_more;
	  if (maybeAddValidJumpSquares(currentSquare, jumpSquares)) {
	    moveStack.peek().setSwitchPlayer(false);
	    selectSquare(currentSquare);
	    highlightValidSquares();
	    kill_more = true;
	  } else {
	    moveStack.peek().setSwitchPlayer(true);
	    switchPlayer();
	    deselectSquare();
	    recomputeValidSquares();
	    kill_more = false;
	  }
	  
	  return kill_more ? KillState.KILL_MORE : KillState.KILL_ONE;
	}
	
	public void doMove(int x, int y) {
	  if (x < 0 || y < 0 || x >= board.size() || y >= board.size()) {
	    return;
	  }
	  
		Square currentSquare = board.getSquare(x, y);
		
		if (sticky) {
		  KillState killState = maybeKillPiece(currentSquare);
		  if (killState == KillState.KILL_ONE) {
		    // We can't kill more so set sticky to false.
		    sticky = false;
		    moveStack.peek().setFlipSticky(true);
		  }
		  return;
		}
		
		if (currentSquare == selectedSquare) {
			deselectSquare();
		  recomputeValidSquares();
		  return;
		}
		
		if (selectedSquare != null) {
		  if (currentSquare.isEmptySquare()) {
				// If we have square selected and an empty square is touched.
				if (moveSquares.contains(currentSquare)) {
				  // First save the undo state.
					moveStack.push(new Move(selectedSquare, currentSquare, null));
					moveStack.peek().setSwitchPlayer(true);
					
					// Move the current player's piece.
					currentSquare.setPiece(selectedSquare.getPiece());
					selectedSquare.setEmptySquare();
					switchPlayer();
					
					// De-select the square and find new moves.
					deselectSquare();
					recomputeValidSquares();
				} else {
				  KillState killState = maybeKillPiece(currentSquare);
				  switch (killState) {
				    case NO_KILL:
							deselectSquare();
						  recomputeValidSquares();
				      break;
				    case KILL_ONE:
				      break;
				    case KILL_MORE:
				      moveStack.peek().setFlipSticky(true);
				      sticky = true;
				      break;
				  }
				}
			} else {
			  deselectSquare();
				recomputeValidSquares();
			}
		}
		
		if (movePieces.contains(currentSquare)) {
			selectSquare(currentSquare);
		  recomputeValidSquares();
		}
	}

	private Square killPiece(Square startSquare, Square endSquare) {
		int xDiff = endSquare.getX() - startSquare.getX() > 0 ? 1 : -1;
		int yDiff = endSquare.getY() - startSquare.getY() > 0 ? 1 : -1;
		
		Square killSquare = null;
		int x = startSquare.getX() + xDiff;
		int y = startSquare.getY() + yDiff;
		for (; x != endSquare.getX(); x += xDiff, y += yDiff) {
			Square square = board.getSquare(x, y);
			if (!square.isEmptySquare()) {
				killSquare = square;
				square.setEmptySquare();
			}
		}
		
		return killSquare;
	}
  public void undoMove() {
    if (moveStack.empty()) {
      return;
    }
    
    Move lastMove = moveStack.pop();
    Piece movedPiece = lastMove.getDestSquare().getPiece();
    assert movedPiece != null;
    lastMove.getSrcSquare().setPiece(movedPiece);
    lastMove.getDestSquare().setEmptySquare();
    deselectSquare();
    
    Square killSquare = lastMove.getKillSquare();
    if (killSquare != null) {
      killSquare.setPiece(movedPiece.isWhite() ? Piece.getBlackPiece() : Piece.getWhitePiece());
    }
    
    if (lastMove.shouldSwitchPlayer()) {
      switchPlayer();
    } 
    
    if (lastMove.shouldFlipSticky()) {
      sticky = !sticky;
    }
    
    recomputeValidSquares();
  }
}
