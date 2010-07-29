package com.android.checkers;

import android.app.Activity;
import android.os.Bundle;

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
}