package com.example.musifly;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class notificationService
{

    public static PlayerNotificationManager playerNotificationManager;
    private final int notificationID = 1234;
    String name;
    String photo;
    Context context;
    Bitmap img;
    notificationService(Context context,String name,String photo)
    {
        this.name = name;
        this.photo = photo;
        this.context = context;
    }
    private PlayerNotificationManager.MediaDescriptionAdapter mediaDescriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return "Musifly";
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return null;
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return name;
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        img = Glide.with(context).asBitmap().load(photo).submit().get();
                    }
                    catch (ExecutionException | InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            return img;
        }
    };
    public void create_notification()
    {
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context, "my_channel", R.string.music, notificationID, mediaDescriptionAdapter, new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {

            }

            @Override
            public void onNotificationCancelled(int notificationId) {

            }

            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {

            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {

            }
        });
        playerNotificationManager.setPlayer(bottom_player.exoPlayer);
    }
    public static void destroy()
    {
        if(playerNotificationManager!=null)
        {
            playerNotificationManager.setPlayer(null);
        }
        if(bottom_player.exoPlayer!=null)
        {
            bottom_player.exoPlayer.release();
            bottom_player.exoPlayer = null;
        }
    }


}


