package com.example.musifly;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment.SavedState;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class bhakti_list_Adapter extends RecyclerView.Adapter<bhakti_list_Adapter.ViewHolder> {
    ArrayList<String> songs_id = new ArrayList<>();
    ArrayList<String> songs_name = new ArrayList<>();
    ArrayList<String> songs_images = new ArrayList<>();
    FragmentManager manager;
    Context context;
    public static SavedState savedState;
    public static fragment_class fragment;

    bhakti_list_Adapter(Context context, ArrayList<String> songs_id, ArrayList<String> songs_name, ArrayList<String> songs_images, FragmentManager manager) {
        this.context = context;
        this.songs_id = songs_id;
        this.songs_images = songs_images;
        this.songs_name = songs_name;
        this.manager = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View items = inflater.inflate(R.layout.song_layout, parent, false);
        return new ViewHolder(items);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.song_name.setText(songs_name.get(position));
        Glide.with(context).load(songs_images.get(position)).placeholder(R.drawable.ic_baseline_music_note_24).error(R.drawable.ic_launcher_background).into(holder.song_image);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.jiosaavn.com/api.php?__call=song.getDetails&cc=in\\&_marker=0%3F_marker%3D0&_format=json&pids=" + songs_id.get(position);
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject result = new JSONObject(jsonObject.getString(songs_id.get(position)));

                            if (bottom_player.exoPlayer != null && bottom_player.exoPlayer.isPlaying()) {
                                bottom_player.exoPlayer.stop();
                                bottom_player.exoPlayer.release();
                            }

                            //bottom_player player = new bottom_player(context,result.getString("media_preview_url"),songs_images.get(position),songs_name.get(position));
                            bottom_player player = bottom_player.getInstance(context,result.getString("media_preview_url"),songs_images.get(position),songs_name.get(position));
                            notificationService service = new notificationService(context,songs_name.get(position),songs_images.get(position));

                            player.update_views();
                            player.play();
                            player.events();
                            service.create_notification();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                requestQueue.add(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs_images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView song_image;
        TextView song_name;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.song_image = (ImageView) itemView.findViewById(R.id.song_image);
            this.song_name = (TextView) itemView.findViewById(R.id.song_name);
            this.layout = (LinearLayout) itemView.findViewById(R.id.songs);
        }
    }
}

