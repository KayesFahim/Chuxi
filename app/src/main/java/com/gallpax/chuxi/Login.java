package com.gallpax.chuxi;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;



public class Login extends AppCompatActivity {

    public static int TIMEOUT_MS=10000;
    EditText EmailText, PasswordText;
    Button loginBtn;
    String BaseUrl = "https://api.central.flyfarint.com/v.1.0/EmployeeLogin.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("usersData", MODE_PRIVATE);
        String check = prefs.getString("authenticated", "");

        if (check.equals("true")) {
            startActivity(new Intent(Login.this, MainActivity.class));
        } else {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {

                setContentView(R.layout.activity_login);
                EmailText = findViewById(R.id.EmailTextInput);
                PasswordText = findViewById(R.id.PasswordTextInput);
                loginBtn = findViewById(R.id.loginBtn);

                loginBtn.setOnClickListener(view -> {
                    ProgressDialog progress = new ProgressDialog(Login.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();

                    String email = EmailText.getText().toString();
                    String password = PasswordText.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                        Toast.makeText(getApplicationContext(), "Enter Valid email address!", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    } else if ((!(TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) && !(TextUtils.isEmpty(password))) {

                        String url = BaseUrl + "EmployeeEmail=" + email + "&EmployeePassword=" + password;


                        JsonObjectRequest jsonRequest = new JsonObjectRequest
                                (Request.Method.GET, url, null, response -> {
                                    try {

                                        if (response.getString("message").equals("success")) {

                                            SharedPreferences.Editor data = getSharedPreferences("usersData", MODE_PRIVATE).edit();
                                            data.putString("companyid", response.getString("companyid"));
                                            data.putString("employeeid", response.getString("employeeid"));
                                            data.putString("name", response.getString("name"));
                                            data.putString("email", response.getString("email"));
                                            data.putString("phone", response.getString("phone"));
                                            data.putString("designation", response.getString("designation"));
                                            data.putString("department", response.getString("department"));
                                            data.putString("checkin", response.getString("checkin"));
                                            data.putString("checkout", response.getString("checkout"));
                                            data.putString("ip", response.getString("ip"));
                                            data.putString("authenticated", "true");
                                            data.apply();
                                            progress.dismiss();
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                        } else {
                                            progress.dismiss();
                                            Toast.makeText(getApplicationContext(), "Login Credentials Invalid", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {
                                    progress.dismiss();
                                    error.printStackTrace();
                                });

                        Volley.newRequestQueue(Login.this).add(jsonRequest);
                        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

                    } else {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Unknown error!", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {

                Intent myIntent = new Intent(Login.this, ErrorActivity.class);
                myIntent.putExtra("activity", MainActivity.class.getSimpleName());
                startActivity(myIntent);
            }


        }
    }


}