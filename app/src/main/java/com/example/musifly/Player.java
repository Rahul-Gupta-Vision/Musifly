package com.example.musifly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class Player extends AppCompatActivity  {

    SharedPreferences sharedPreferences;

    public static SimpleExoPlayer simpleExoPlayer;
    TextView songname;
    ImageView music_photo;
    SeekBar music_bar;
    Handler mhandler ;
    ImageView play_pause_btn;
    String url;
    String name;
    String photo;
    public static Activity activity;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mhandler = new Handler();
        activity = this;

       intent = getIntent();

        if(intent.getStringExtra("music")==null && intent.getStringExtra("music_name")==null && intent.getStringExtra("music_photo")==null)
        {
            sharedPreferences = getSharedPreferences("mypreference", MODE_PRIVATE);
            url = sharedPreferences.getString("music","");
            name = sharedPreferences.getString("name","");
            photo = sharedPreferences.getString("photo","");

        }
        else
        {



            url = intent.getStringExtra("music");
            name = intent.getStringExtra("music_name");
            photo = intent.getStringExtra("music_photo");
        }

        url = url.replace("preview.saavncdn.com", "sklktcdnems01.cdnsrv.jio.com/aac.saavncdn.com");
        url = url.replace("_96_p.mp4", "_320.mp4");
        songname = findViewById(R.id.music_name);
        music_bar = findViewById(R.id.music_bar);
        music_photo = findViewById(R.id.music_photo);
        play_pause_btn = findViewById(R.id.play_btn);
        songname.setText(name);
        Glide.with(this).load(photo).placeholder(R.drawable.ic_baseline_music_note_24).error(R.drawable.ic_launcher_background).into(music_photo);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        DataSource.Factory datasFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this,"app"));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(datasFactory).createMediaSource(Uri.parse(url));
        simpleExoPlayer.prepare(mediaSource);
        if(simpleExoPlayer.isPlaying() && simpleExoPlayer!=null)
        {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            play_pause_btn.setImageResource(R.drawable.play);
        }
        else
        {

            simpleExoPlayer.setPlayWhenReady(true);
            play_pause_btn.setImageResource(R.drawable.pause);
        }

        play_pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!simpleExoPlayer.isPlaying())
                {
                    simpleExoPlayer.setPlayWhenReady(true);
                    play_pause_btn.setImageResource(R.drawable.pause);
                }
                else
                {
                    simpleExoPlayer.setPlayWhenReady(false);
                    play_pause_btn.setImageResource(R.drawable.play);
                }
            }
        });

        simpleExoPlayer.addListener(new com.google.android.exoplayer2.Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == simpleExoPlayer.STATE_READY)
                {
                    long time = simpleExoPlayer.getDuration();
                    music_bar.setMax((int) (time/1000));
                }
                if(playbackState == simpleExoPlayer.STATE_ENDED)
                {
                    play_pause_btn.setImageResource(R.drawable.play);
                    simpleExoPlayer.seekTo(0);
                    music_bar.setProgress(0);
                }
            }
        });


        Player.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(simpleExoPlayer!=null)
                {
                    int currpos = (int) (simpleExoPlayer.getCurrentPosition()/1000);
                    music_bar.setProgress(currpos);
                }
                mhandler.postDelayed(this,1000);
            }
        });

        music_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(simpleExoPlayer!=null && fromUser)
                {
                    simpleExoPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }




    @Override
    public void onBackPressed() {
       super.onBackPressed();





        sharedPreferences = getSharedPreferences("mypreference", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("music_url", intent.getStringExtra("music"));
        editor.putString("name", intent.getStringExtra("music_name"));
        editor.putString("photo", intent.getStringExtra("music_photo"));
        editor.apply();
        //Player.activity.finish();
    }
}