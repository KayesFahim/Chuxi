package com.gallpax.chuxi;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Attendance extends AppCompatActivity {

    ProgressBar progressBar;
    CountDownTimer cTimer = null;
    Button CheckInBtn;
    EditText noteText;
    Button b;
    TextView t,currentTime;
    String getIP, networkip;
    String companyid, employeeid, Loaction, checkin, checkout, note;

    String BaseUrl = "https://api.central.flyfarint.com/v.1.0/Attendance.php?";

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        String Localtime = new SimpleDateFormat("hh : mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());


        CheckInBtn = findViewById(R.id.checkin);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));


        t = findViewById(R.id.location);
        b = findViewById(R.id.tryagain);
        b.setVisibility(View.INVISIBLE);
        noteText = findViewById(R.id.note);
        noteText.setVisibility(View.INVISIBLE);

        if (TextUtils.isEmpty(noteText.getText().toString())){
            note = "No Note";
        }else{
            note = noteText.getText().toString();
        }



        currentTime = findViewById(R.id.currentTime);
        currentTime.setText(Localtime);


        SharedPreferences prefs = getSharedPreferences("usersData", MODE_PRIVATE);
        companyid = prefs.getString("companyid", null);
        employeeid = prefs.getString("employeeid", null);
        checkin = prefs.getString("checkin", null);
        checkout = prefs.getString("checkout", null);
        networkip = prefs.getString("ip", "");

        //Now Time
        String TimeNow = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());
        String[] arrayTimeNow = TimeNow.split(":", 2);
        String nowHours = arrayTimeNow[0];
        String nowMinutes = arrayTimeNow[1];
        int nowTotalMinutes = (Integer.parseInt(nowHours) * 60) + Integer.parseInt(nowMinutes);


        //CheckIn Time
        String[] checkInArray = checkin.split(":", 2);
        String checkInHours = checkInArray[0];
        String checkInMinutes = checkInArray[1];
        int checkInTotalMinutes = (Integer.parseInt(checkInHours) * 60) + Integer.parseInt(checkInMinutes);


        //CheckOUT Time
        String[] checkOutArray = checkout.split(":", 2);
        String checkOutHours = checkOutArray[0];
        String checkOutMinutes = checkOutArray[1];
        int checkOutTotalMinutes = (Integer.parseInt(checkOutHours) * 60) + Integer.parseInt(checkOutMinutes);



        if (nowTotalMinutes > checkInTotalMinutes){
            noteText.setVisibility(View.VISIBLE);
        }else if (nowTotalMinutes > checkOutTotalMinutes){
            noteText.setVisibility(View.VISIBLE);
        }




        //Check IP

        URL connection = null;
        try {
            connection = new URL("https://checkip.amazonaws.com/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection con = null;
        try {
            con = connection.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            try {
                getIP = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       // System.out.println("Get API : " +getIP);
        //System.out.println("network API : " +networkip);


        if (getIP.equals(networkip)) {
            t.setText("Your Location: Office");
            Loaction = "Office";
        } else {
            t.setText("Your Location: Home");
            Loaction = "Home";
            b.setVisibility(View.INVISIBLE);
        }


        CheckInBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    progressBar.setVisibility(View.VISIBLE);
                    startTimer();

                }else{if(event.getAction() == MotionEvent.ACTION_UP){
                    progressBar.setProgress(0);
                    cancelTimer();
                }

                }
                return true;
            }

            private void cancelTimer() {
                if(cTimer!=null)
                    cTimer.cancel();
            }

            private void startTimer() {
                cTimer = new CountDownTimer(5000, 1) {
                    public void onTick(long millisUntilFinished) {
                        progressBar.setProgress(progressBar.getProgress() + 1);

                        if(progressBar.getProgress() == 100){
                            String url = BaseUrl+"eId="+employeeid+"&cId="+companyid+"&location="+Loaction+"&note="+note;
                            System.out.println(url);

                            JsonObjectRequest jsonRequest = new JsonObjectRequest
                                    (Request.Method.GET, url, null, response -> {
                                        try {

                                            String message = response.getString("message");
                                            System.out.println(message);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }, error -> {
                                        error.printStackTrace();
                                    });

                            Volley.newRequestQueue(Attendance.this).add(jsonRequest);
                            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000, -1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            progressBar.setVisibility(View.INVISIBLE);
                            CheckInBtn.setVisibility(View.INVISIBLE);

                        }


                    }
                    public void onFinish() {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.INVISIBLE);
                        cancelTimer();
                    }
                };
                cTimer.start();

            }
        });

    }

    private void CheckInAction() {

        //DADA
        String url = BaseUrl+"eId="+employeeid+"&cId="+companyid+"&location="+Loaction+"&note="+note;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {

                        String message = response.getString("message");
                        System.out.println(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        Volley.newRequestQueue(Attendance.this).add(jsonRequest);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, 1.0f));


    }


}
