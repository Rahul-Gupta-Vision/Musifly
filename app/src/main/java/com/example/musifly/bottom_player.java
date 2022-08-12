package com.example.musifly;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class bottom_player
{
    Context context;
    Handler mhandler;
    Runnable task;
    public static SimpleExoPlayer exoPlayer;
    public static String url="";
    public static String photo;
    public static String name;
    ImageView music_photo;
    ImageView play_btn;
   public static TextView music_name;
    SeekBar music_bar;
    RelativeLayout player_layout;
    bottom_player(Context context,String url,String photo,String name)
    {
        this.context = context;
        bottom_player.url = url;
        bottom_player.photo = photo;
        bottom_player.name = name;
    }
    public static bottom_player getInstance(Context con,String murl,String mphoto,String mname)
    {
        if(url.equals(murl))
        {
            return null;
        }
        return new bottom_player(con,murl,mphoto,mname);
    }
    public void update_views()
    {
        music_photo = ((Activity)context).findViewById(R.id.photo);
        music_name = ((Activity)context).findViewById(R.id.music_name);
        music_bar = ((Activity)context).findViewById(R.id.music_bar);
        play_btn = ((Activity)context).findViewById(R.id.play_btn);
        player_layout = ((Activity)context).findViewById(R.id.player_layout);
        url = url.replace("preview.saavncdn.com", "sklktcdnems01.cdnsrv.jio.com/aac.saavncdn.com");
        url = url.replace("_96_p.mp4", "_160.mp4");
        Glide.with(context).load(photo).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.ic_baseline_music_note_24).error(R.drawable.ic_launcher_background).into(music_photo);
        music_name.setText(name);
        music_name.setSelected(true);
        music_name.setSingleLine();
        player_layout.setVisibility(View.VISIBLE);
    }

    public void play()
    {
        exoPlayer = new SimpleExoPlayer.Builder(context).build();
        DataSource.Factory datasFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context,"app"));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(datasFactory).createMediaSource(Uri.parse(url));
        exoPlayer.prepare(mediaSource);
        if(exoPlayer.isPlaying() && exoPlayer!=null)
        {
            exoPlayer.stop();
            exoPlayer.release();
            play_btn.setImageResource(R.drawable.play);
        }
        else
        {
            exoPlayer.setPlayWhenReady(true);
            play_btn.setImageResource(R.drawable.pause);
        }

    }
    public void events() {
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exoPlayer.isPlaying()) {
                    exoPlayer.setPlayWhenReady(true);
                    play_btn.setImageResource(R.drawable.pause);
                } else {
                    exoPlayer.setPlayWhenReady(false);
                    play_btn.setImageResource(R.drawable.play);
                }
            }

        });

        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == exoPlayer.STATE_READY) {
                    Toast.makeText(context,"Ready",Toast.LENGTH_LONG).show();
                    long time = exoPlayer.getDuration();
                    music_bar.setMax((int) (time / 1000));
                }
                if (playbackState == exoPlayer.STATE_ENDED) {
                    play_btn.setImageResource(R.drawable.play);
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.seekTo(0);
                }
            }
        });
        starttimer();
        music_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(exoPlayer!=null && fromUser)
                {
                    exoPlayer.seekTo(progress*1000);
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

    public void starttimer()
    {
        mhandler = new Handler();
        task = new Runnable()
        {

            @Override
            public void run() {
                if(exoPlayer!=null)
                {
                    int currpos = (int) (exoPlayer.getCurrentPosition()/1000);
                    music_bar.setProgress(currpos);
                }
                mhandler.postDelayed(task,1000);
            }
        };
        task.run();
    }

}
