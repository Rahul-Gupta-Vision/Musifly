package com.example.musifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView bhakti_list,bollywood_list,punjabi_list,tamil_list;
    FragmentManager manager;
    public static Context mContext;
    RelativeLayout player_layout;
    ScrollView scrollView;
    fragment_class obj;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        notificationService.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.music_player)
        {
            search_bar_Adapter obj = new search_bar_Adapter(MainActivity.this);
                Intent intent = new Intent(MainActivity.this,Search_Bar.class);

                startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        bhakti_list = findViewById(R.id.bhakti_list);
        bollywood_list = findViewById(R.id.bollywood_list);
        punjabi_list = findViewById(R.id.punjabi_list);
        tamil_list = findViewById(R.id.tamil_list);
        player_layout = findViewById(R.id.player_layout);
        scrollView = findViewById(R.id.scrollview);
        manager = getSupportFragmentManager();
        mContext = MainActivity.this;
        if(bottom_player.exoPlayer == null)
        {
            player_layout.setVisibility(View.INVISIBLE);
        }
        bhakti_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        bollywood_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        punjabi_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        tamil_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        songs("bhakti%20songs",bhakti_list);
        songs("arijit%20singh",bollywood_list);
        songs("punjabi%20party",punjabi_list);
        songs("tamil%20love",tamil_list);
    }

    public void songs(String name,RecyclerView view)
    {
         ArrayList<String> songs_id = new ArrayList<>();
         ArrayList<String> songs_name = new ArrayList<>();
         ArrayList<String> songs_images = new ArrayList<>();
        String URL = "https://www.jiosaavn.com/api.php?p=1&q="+name+"\\%20&_format=json&_marker=0&api_version=4&ctx=wap6dot0\\%20&n=20&__call=search.getResults";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray result = jsonObject.getJSONArray("results");
                    for(int i=0;i<result.length();i++)
                    {
                        JSONObject object = new JSONObject(result.get(i).toString());
                        songs_id.add(object.getString("id"));
                        songs_name.add(object.getString("title"));
                        songs_images.add(object.getString("image"));
                    }
                    view.setAdapter(new bhakti_list_Adapter(MainActivity.this,songs_id,songs_name,songs_images,manager));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);

    }



}