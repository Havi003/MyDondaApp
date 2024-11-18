package com.firstapp.testdondaapp.ui.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firstapp.testdondaapp.R;

public class workingHours extends Fragment {

    private TextView timerTextView;
    private Button startButton, stopButton;
    private Handler handler;
    private Runnable timerRunnable;

    private long startTime = 0L;
    private boolean isTimerRunning = false;

    public workingHours() {
        // Required empty public constructor
    }

    public static workingHours newInstance(String param1, String param2) {
        workingHours fragment = new workingHours();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_working_hours, container, false);

        timerTextView = view.findViewById(R.id.timerTextView);
        startButton = view.findViewById(R.id.startButton);
        stopButton = view.findViewById(R.id.stopButton);
        handler = new Handler();

        // Start the timer when "Start Job" button is clicked
        startButton.setOnClickListener(v -> startTimer());

        // Stop the timer when "Stop Job" button is clicked
        stopButton.setOnClickListener(v -> stopTimer());

        return view;
    }

    private void startTimer() {
        if (isTimerRunning) return; // Prevent starting the timer if already running

        isTimerRunning = true;
        startTime = SystemClock.elapsedRealtime();

        // Disable "Start Job" button and enable "Stop Job" button
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = SystemClock.elapsedRealtime() - startTime;
                int hours = (int) (elapsedMillis / (1000 * 60 * 60));
                int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);
                int seconds = (int) (elapsedMillis / 1000) % 60;

                // Update timer TextView with the formatted time
                String time = String.format("WORKING HOURS: %02d:%02d:%02d", hours, minutes, seconds);
                timerTextView.setText(time);

                // Continue updating every second
                handler.postDelayed(this, 1000);
            }
        };

        // Start the timer update
        handler.post(timerRunnable);
    }

    private void stopTimer() {
        if (!isTimerRunning) return; // Avoid stopping if the timer is not running

        isTimerRunning = false;
        handler.removeCallbacks(timerRunnable); // Stop the timer

        // Calculate the final elapsed time
        long elapsedMillis = SystemClock.elapsedRealtime() - startTime;
        int hours = (int) (elapsedMillis / (1000 * 60 * 60));
        int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);
        int seconds = (int) (elapsedMillis / 1000) % 60;
        String finalTime = String.format("Job Completed: %02d:%02d:%02d", hours, minutes, seconds);

        // Display final time and show a toast message
        timerTextView.setText(finalTime);
        Toast.makeText(getActivity(), "Timer stopped at: " + finalTime, Toast.LENGTH_SHORT).show();

        // Re-enable "Start Job" button and disable "Stop Job" button
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable); // Clean up the handler to prevent memory leaks
        }
    }
}
