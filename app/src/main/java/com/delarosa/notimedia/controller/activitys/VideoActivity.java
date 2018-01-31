package com.delarosa.notimedia.controller.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.delarosa.notimedia.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends Activity {

    private int position = 0;
    @BindView(R.id.videoView)
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ButterKnife.bind(this);
        setUpVideoView();
    }

    //prepares the video and the double tap listener
    @SuppressLint("ClickableViewAccessibility")
    private void setUpVideoView() {

        String uriPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Arkbox.mp4";
        Uri uri = Uri.parse(uriPath);
        MediaController mediaController = new MediaController(this);

       // videoView.setMediaController(mediaController);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(VideoActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Intent i = new Intent(VideoActivity.this, NotificationManagerActivity.class);
                    startActivity(i);
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent event) {
                    Log.d("TEST", "onSingleTap");
                    return false;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }


        });

        try {

            videoView.setVideoURI(uri);
            videoView.requestFocus();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.setOnPreparedListener(videoViewListener);//listener video ready
    }

    //listener of the video
    private MediaPlayer.OnPreparedListener videoViewListener =
            new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    mediaPlayer.setLooping(true);
                    if (position == 0) {
                        //if we have a position since savedInstanceState the video should start from here
                        videoView.start();
                    } else {
                        // if we come from activity resume the video will pause
                        videoView.pause();
                    }

                }
            };


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", videoView.getCurrentPosition());
        videoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        videoView.seekTo(position);
    }


}