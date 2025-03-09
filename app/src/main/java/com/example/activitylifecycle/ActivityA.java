package com.example.activitylifecycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Main Activity demonstrating lifecycle management and background threading in Android.
 */
public class ActivityA extends Activity {
    // Constants
    private static final String TAG = "ActivityA";
    private static final String KEY_RESTART_COUNT = "RestartCount";
    private static final String KEY_DIALOG_SHOWN = "DialogWasShown";
    private static final int THREAD_UPDATE_INTERVAL_MS = 1000;

    // UI Components
    private TextView restartCountView;
    private TextView threadCountView;

    // State variables
    private int restartCount = 0;
    private int threadCounter = 1;
    private boolean dialogWasShown = false;

    // Background thread
    private Thread counterThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        // Initialize UI components
        initializeViews();

        // Restore state if available
        restoreState(savedInstanceState);

        // Create the background thread
        createBackgroundThread();

        // Update UI with current values
        updateRestartCount();
    }

    /**
     * Initialize view references
     */
    private void initializeViews() {
        restartCountView = findViewById(R.id.restart_counter);
        threadCountView = findViewById(R.id.thread_counter);
    }

    /**
     * Restore activity state from saved instance or intent
     */
    private void restoreState(Bundle savedInstanceState) {
        // First check saved instance state (e.g., from configuration changes)
        if (savedInstanceState != null) {
            restartCount = savedInstanceState.getInt(KEY_RESTART_COUNT, 0);
            dialogWasShown = savedInstanceState.getBoolean(KEY_DIALOG_SHOWN, false);
        }

        // Then check if values were passed via intent (takes precedence)
        int restartCountFromIntent = getIntent().getIntExtra(KEY_RESTART_COUNT, 0);
        if (restartCountFromIntent > restartCount) {
            restartCount = restartCountFromIntent;
        }
    }

    /**
     * Creates and starts the background thread that updates the counter
     */
    private void createBackgroundThread() {
        counterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinishing()) {
                    try {
                        Thread.sleep(THREAD_UPDATE_INTERVAL_MS);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateThreadCounter(1);
                            }
                        });
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Background thread interrupted", e);
                        break;
                    }
                }
            }
        });
        counterThread.start();
    }

    /**
     * Updates the thread counter by the specified increment
     */
    private void updateThreadCounter(int increment) {
        threadCounter += increment;
        String counterText = String.format("%04d", threadCounter);
        threadCountView.setText("Thread Counter: " + counterText);
    }

    /**
     * Updates the restart count display
     */
    private void updateRestartCount() {
        restartCountView.setText("Restart Count: " + restartCount);
    }

    // Lifecycle methods

    @Override
    protected void onStart() {
        super.onStart();
        updateRestartCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        // Handle special case for dialog
        if (dialogWasShown) {
            restartCount++;
            updateRestartCount();
            dialogWasShown = false;
            Log.d(TAG, "Counter incremented due to dialog, new count: " + restartCount);
        }
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

        // Check if intent has a newer restart count
        int restartCountFromIntent = getIntent().getIntExtra(KEY_RESTART_COUNT, 0);
        if (restartCountFromIntent > restartCount) {
            restartCount = restartCountFromIntent;
        }

        // Increment restart count and update UI
        restartCount++;
        updateRestartCount();
        Log.d(TAG, "onRestart called, new count: " + restartCount);
    }

    @Override
    protected void onDestroy() {
        // Clean up thread resources
        if (counterThread != null) {
            counterThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_RESTART_COUNT, restartCount);
        outState.putBoolean(KEY_DIALOG_SHOWN, dialogWasShown);
    }

    // Button click handlers

    public void startActivityB(View v) {
        updateThreadCounter(5);
        startActivity(new Intent(this, ActivityB.class));
    }

    public void startActivityC(View v) {
        updateThreadCounter(10);
        startActivity(new Intent(this, ActivityC.class));
    }

    public void showDialog(View v) {
        dialogWasShown = true;
        startActivity(new Intent(this, DialogActivity.class));
        overridePendingTransition(0, 0);  // No animation for dialog
    }

    public void finishActivityA(View v) {
        finish();
    }
}