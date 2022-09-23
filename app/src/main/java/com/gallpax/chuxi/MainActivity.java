package com.gallpax.chuxi;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView dateText, greetText , userName , userDegignation;
    String ip;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMM Y");
        LocalDateTime now = LocalDateTime.now();

        dateText = findViewById(R.id.date);
        dateText.setText(dtf.format(now));

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        greetText = findViewById(R.id.greetings_text);

        if (timeOfDay < 12) {
            greetText.setText("Good Morning");
        } else if (timeOfDay < 16) {
            greetText.setText("Good Afternoon");
        } else if (timeOfDay < 21) {
            greetText.setText("Good Evening");
        } else {
            greetText.setText("Good Night");
        }

        //Retrive Data

        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Getting Data");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        SharedPreferences prefs = getSharedPreferences("usersData", MODE_PRIVATE);
        String companyid = prefs.getString("name", "No name defined");
        String employeeid = prefs.getString("name", "No name defined");
        String email = prefs.getString("email", "No name defined");
        String name = prefs.getString("name", "No name defined");
        String phone = prefs.getString("phone", "No name defined");
        String designation = prefs.getString("designation", "No name defined");
        String department = prefs.getString("department", "No name defined");
        String checkin = prefs.getString("checkin", "No name defined");
        String checkout = prefs.getString("checkout", "No name defined");
        progress.dismiss();

        userName = findViewById(R.id.name);
        userName.setText(name);
        userDegignation = findViewById(R.id.designation);
        userDegignation.setText(designation);

    }


    public void CheckIn(View view) {
        startActivity(new Intent(this, Attendance.class));
    }

    @SuppressLint("ApplySharedPref")
    public void LogOut(View view) {
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Logging Out");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        SharedPreferences prefs = getSharedPreferences("usersData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        startActivity(new Intent(this, Login.class));


    }
}