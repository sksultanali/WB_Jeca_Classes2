package com.developerali.wbjecaclasses.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;

import com.developerali.wbjecaclasses.Login;
import com.developerali.wbjecaclasses.R;
import com.developerali.wbjecaclasses.databinding.ActivityReadBookBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadBook extends AppCompatActivity {

    ActivityReadBookBinding binding;
    FirebaseDatabase data;
    FirebaseAuth auth;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        data = FirebaseDatabase.getInstance();
        auth =FirebaseAuth.getInstance();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        binding.progressBar4.setVisibility(View.VISIBLE);

        String url = getIntent().getStringExtra("url");
        new getPDF().execute(url);

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

        resetTimer();


    }

    private void checkLogin() {
        if (auth.getCurrentUser() != null){
            String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            data.getReference().child("loggers")
                    .child(auth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String id = snapshot.getValue(String.class);
                                if (!id.equalsIgnoreCase(deviceId)){
                                    auth.signOut();
                                    finish();
                                    Snackbar snackbar = Snackbar.make(binding.linearLayout2, "Another Login Detect...", Snackbar.LENGTH_LONG)
                                            .setAction("Login Again", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent i = new Intent(ReadBook.this, Login.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            });
                                    snackbar.show();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    void resetTimer() {
        timer = new CountDownTimer(120000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                checkLogin();
            }
        };
    }
    class getPDF extends AsyncTask<String, Void, InputStream>{

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            URL url = null;
            try {
                url = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
//            binding.pdfView.fromStream(inputStream).load();
            binding.progressBar4.setVisibility(View.GONE);
        }
    }




}