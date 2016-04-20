package com.serigon.moviezy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.serigon.moviezy.activity.MovieActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MovieActivity.class);
        startActivity(intent);
        finish();
    }

}
