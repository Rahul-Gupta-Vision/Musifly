package com.example.musifly;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class fragment_class extends DialogFragment
{
    private static fragment_class obj;
    TextView songname;
    ImageView songphoto;
    String url = null;
    String name = null;
    String photo = null;
    public static SimpleExoPlayer exoPlayer;
    Context context;
    fragment_class(String url,String name,String photo,Context context)
    {
        this.url = url;
        this.name = name;
        this.photo = photo;
        this.context = context;
    }
    fragment_class()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.player_fragement,container,false);
        songname = rootview.findViewById(R.id.music_name);
        songphoto = rootview.findViewById(R.id.music_photo);
        exoPlayer = new SimpleExoPlayer.Builder(context).build();
        if(url!=null && name!=null && photo!=null && context!=null)
        {
            if(savedInstanceState!=null)
            {
                url = savedInstanceState.getString("url");
                url = url.replace("preview.saavncdn.com", "sklktcdnems01.cdnsrv.jio.com/aac.saavncdn.com");
                url = url.replace("_96_p.mp4", "_160.mp4");
                songname.setText(savedInstanceState.getString("name"));
                Glide.with(this).load(savedInstanceState.getString("photo")).placeholder(R.drawable.ic_baseline_music_note_24).error(R.drawable.ic_launcher_background).into(songphoto);
            }
            else {
                url = url.replace("preview.saavncdn.com", "sklktcdnems01.cdnsrv.jio.com/aac.saavncdn.com");
                url = url.replace("_96_p.mp4", "_160.mp4");
                songname.setText(name);
                Glide.with(this).load(photo).placeholder(R.drawable.ic_baseline_music_note_24).error(R.drawable.ic_launcher_background).into(songphoto);
            }
            init_player(exoPlayer,url);
        }
        else
        {
            Toast.makeText(getActivity().getApplication(),"Error in Fetching data!!",Toast.LENGTH_LONG).show();
        }

        if(exoPlayer.isPlaying())
        {
            exoPlayer.stop();
            exoPlayer.release();
        }
        else
        {
            exoPlayer.setPlayWhenReady(true);
        }

        return rootview;
    }
    void init_player(SimpleExoPlayer exoPlayer,String url)
    {
        DataSource.Factory datasFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context,"app"));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(datasFactory).createMediaSource(Uri.parse(url));
        exoPlayer.prepare(mediaSource);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url",url);
        outState.putString("name",name);
        outState.putString("photo",photo);
    }
}
