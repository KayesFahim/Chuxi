package com.gallpax.chuxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ErrorActivity extends AppCompatActivity {

    String backActivity,backClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Intent mIntent = getIntent();
        backActivity = mIntent.getStringExtra("activity");

        backClass = backActivity+".class";


    }


    public void TryAgain(View view) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {

            Toast.makeText(getApplicationContext(),"Network Connection is ON", Toast.LENGTH_LONG).show();

            try {
                Class newClass = Class.forName(backClass);
                Intent resume = new Intent(this, newClass);
                startActivity(resume);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            }else {
            Toast.makeText(getApplicationContext(),"Network Connection is OFF", Toast.LENGTH_LONG).show();
        }


    }
}