package com.example.musifly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class search_bar_Adapter extends RecyclerView.Adapter<search_bar_Adapter.ViewHolder> {
    Context context;
    Context mContext;
    ArrayList<String> songid;
    ArrayList<String> songname;
    ArrayList<String> songphoto;
    search_bar_Adapter(Context mContext)
    {
        this.mContext = mContext;
    }
    search_bar_Adapter(ArrayList<String> songid, ArrayList<String> songname, ArrayList<String> songphoto ,Context context)
    {
        this.songid = songid;
        this.songname = songname;
        this.songphoto = songphoto;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View items = inflater.inflate(R.layout.search_list,parent,false);
        return new ViewHolder(items);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(songname.get(position));
        Glide.with(context).load(songphoto.get(position)).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.ic_baseline_music_note_24).error(R.drawable.ic_launcher_background).into(holder.photo);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.jiosaavn.com/api.php?__call=song.getDetails&cc=in\\&_marker=0%3F_marker%3D0&_format=json&pids=" + songid.get(position);
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject result = new JSONObject(jsonObject.getString(songid.get(position)));

                            if (bottom_player.exoPlayer != null && bottom_player.exoPlayer.isPlaying()) {
                                bottom_player.exoPlayer.stop();
                                bottom_player.exoPlayer.release();
                            }
                            bottom_player player = new bottom_player(MainActivity.mContext,result.getString("media_preview_url"),songphoto.get(position),songname.get(position));
                            notificationService service = new notificationService(context,songname.get(position),songphoto.get(position));
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
        return songid.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name;
        LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.song_image);
            name = itemView.findViewById(R.id.song_name);
            layout = itemView.findViewById(R.id.list_layout);
        }
    }
}
