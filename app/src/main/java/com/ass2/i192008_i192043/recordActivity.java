package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

public class recordActivity extends AppCompatActivity {
    static int PERMISSION_CODE = 1;
    static int RECORDING_NUMBER = 0;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    ImageView arrow_back;
    TextView record_time;
    Thread t;
    ImageView stop_icon;
    boolean RecordingStatus;
    SeekBar status_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        arrow_back = findViewById(R.id.arrow_back);
        record_time= findViewById(R.id.record_time);
        stop_icon  = findViewById(R.id.stop_icon);
        status_bar = findViewById(R.id.status_bar);
        RecordingStatus = false;

        addOnclickListener();
        if (isMicrophoneAvailable()) {
            getMicrophonePermission();
        }
        mediaPlayer = new MediaPlayer();
        mediaRecorder = new MediaRecorder();


    }

    // Add OnclickListener
    private void addOnclickListener() {
        arrow_back.setOnClickListener(v -> {
            finish();
        });
        stop_icon.setOnClickListener(v -> {
            startRecording();
        });
    }

    // Play the recording
    private void playRecording() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            try {
                mediaPlayer.setDataSource(getRecordingFilePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(this, "Playing Recording", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error in playing", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Update the record time
    private void updateTextView() {
        String time = record_time.getText().toString();
        int min = Integer.parseInt(time.substring(0, 2));
        int sec = Integer.parseInt(time.substring(3, 5));
        sec++;
        if (sec == 60) {
            min++;
            sec = 0;
        }
        String min_str = String.valueOf(min);
        String sec_str = String.valueOf(sec);
        if (min < 10) {
            min_str = "0" + min_str;
        }
        if (sec < 10) {
            sec_str = "0" + sec_str;
        }
        String str = min_str + ":" + sec_str;
        record_time.setText(str);
    }


    // Start record
    private void startRecording() {
        if (!RecordingStatus) {
            try {
                RECORDING_NUMBER++;
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(getRecordingFilePath());
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
                mediaRecorder.start();
                RecordingStatus = true;
                // update the record time every second
                t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            while (!isInterrupted()) {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateTextView();
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            System.out.println("Thread Interrupted");
                        }
                    }
                };
                t.start();


                Toast.makeText(this, "Recording starts", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error recording", Toast.LENGTH_SHORT).show();
            }
        } else {
            stopRecord();
            RecordingStatus = false;
        }
    }

    // Stop Recording
    private void stopRecord() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Recording stops", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error recording", Toast.LENGTH_SHORT).show();
        }
    }


    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "recording" + (RECORDING_NUMBER) + ".mp3");
        return file.getAbsolutePath();
    }


    // get Microphone permission
    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
        }
    }

    // check microphone
    private boolean isMicrophoneAvailable() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }
}