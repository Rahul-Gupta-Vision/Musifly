package com.example.musifly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Search_Bar extends AppCompatActivity {
    private EditText searchbar;
    private RecyclerView search_list;
    Activity activity;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_search__bar);
        getSupportActionBar().hide();
        searchbar = findViewById(R.id.search);
        search_list = findViewById(R.id.search_list);
        search_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        showKeyboard(searchbar);
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter_list(s.toString());
            }
        });
    }

    public void showKeyboard(View view)
    {
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public  void filter_list(String str)
    {
        ArrayList<String> songs_id = new ArrayList<>();
        ArrayList<String> songs_name = new ArrayList<>();
        ArrayList<String> songs_images = new ArrayList<>();
        String url = "https://www.jiosaavn.com/api.php?p=1&q="+str+"\\%20&_format=json&_marker=0&api_version=4&ctx=wap6dot0\\%20&n=20&__call=search.getResults";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
                    search_list.setAdapter(new search_bar_Adapter(songs_id,songs_name,songs_images,Search_Bar.this));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }
}