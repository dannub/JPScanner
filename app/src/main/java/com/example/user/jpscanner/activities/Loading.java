package com.example.user.jpscanner.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.jpscanner.R;

public class Loading extends AppCompatActivity {
    private static int spash=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Loading.this,MainActivity.class);
                startActivity(i);

                this.finish();
            }
            public  void  finish(){
                Loading.this.finish();
            }
        },spash);
    }
}
