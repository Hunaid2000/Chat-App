package com.ass2.i192008_i192043;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;

public class IncomingCall extends BaseActivity {
    private String mCallId;
    private AudioPlayer mAudioPlayer;


    public static final String ACTION_ANSWER = "answer";
    public static final String ACTION_IGNORE = "ignore";
    public static final String EXTRA_ID = "id";
    public static int MESSAGE_ID = 14;
    private String mAction;

    ImageView profile_image;
    TextView textViewRemoteUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomingcall);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        TextView callState=findViewById(R.id.callState);
        SwipeButton answer = findViewById(R.id.accept_swipe_btn);
        SwipeButton decline = findViewById(R.id.reject_swipe_btn);
        profile_image=findViewById(R.id.incoming_profile_image);
        textViewRemoteUser=findViewById(R.id.remoteUser);
        textViewRemoteUser.setText(getIntent().getStringExtra("name"));
        callState.setText("Incoming Voice Call");

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();


        answer.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                answerClicked();
            }
        });

        decline.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                declineClicked();
            }
        });
    }




    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        finish();
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        finish();
    }
}
