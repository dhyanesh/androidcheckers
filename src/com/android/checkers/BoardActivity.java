package com.android.checkers;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BoardActivity extends Activity {
    private BoardView boardView;
    
    private static String SAVE_KEY = "board-view";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.board);
      
      boardView = (BoardView) findViewById(R.id.board);
      if (savedInstanceState != null) {
        Bundle map = savedInstanceState.getBundle(SAVE_KEY);
        if (map != null) {
          boardView.restoreState(map);
        }
      }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      
      outState.putBundle(SAVE_KEY, boardView.saveState());
    }
    
    @Override
    public void onPause() {
      super.onPause();
    }
    
    @Override
    public void onStop() {
      super.onStop();
    }
    
    @Override
    public void onDestroy() {
      super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.options_menu, menu);
      return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
      menu.findItem(R.id.undo_menu_item).setEnabled(boardView.canUndo());
      return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
        case R.id.player_vs_player:
            boardView.NewGame();
            return true;
        case R.id.computer_vs_player:
            return true;
        case R.id.player_vs_computer:
            return true;
        case R.id.undo_menu_item:
            boardView.undoMove();
            return true;
        case R.id.options_menu_item:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}