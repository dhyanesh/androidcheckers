package com.android.checkers;

import android.os.AsyncTask;

public class GameMoveTask extends AsyncTask<Object, Void, BoardView> {

	@Override
	protected BoardView doInBackground(Object... params) {
		assert params.length == 4;
		Game game = (Game)params[1];
		game.doMove((Integer)params[2], (Integer)params[3]);
		return (BoardView)params[0];
	}

	@Override
	protected void onPostExecute(BoardView result) {
		result.invalidate();
	}
}
