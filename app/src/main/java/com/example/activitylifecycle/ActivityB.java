package com.example.activitylifecycle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Activity B - Demonstrates activity lifecycle behavior and incrementing the thread counter.
 */
public class ActivityB extends Activity {
    // Constants
    private static final String TAG = "ActivityB";
    private static final String KEY_RESTART_COUNT = "RestartCount";

    // UI Components
    private TextView restartCountView;

    // State variables
    private int restartCount = 0;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);

        // Initialize UI components
        initializeViews();

        // Restore state if available
        if (savedInstanceState != null) {
            restartCount = savedInstanceState.getInt(KEY_RESTART_COUNT, 0);
        }

        // Update the UI
        updateRestartCount();

        Log.d(TAG, "onCreate called");
    }

    /**
     * Initialize view references
     */
    private void initializeViews() {
        restartCountView = findViewById(R.id.restart_counter);
    }

    /**
     * Updates the restart count display
     */
    private void updateRestartCount() {
        if (restartCountView != null) {
            restartCountView.setText("Restart Count: " + restartCount);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        restartCount++;
        updateRestartCount();
        Log.d(TAG, "onRestart called, new count: " + restartCount);
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_RESTART_COUNT, restartCount);
    }

    /**
     * Button click handler to finish this activity
     */
    public void finishActivityB(View v) {
        finish();
    }
}