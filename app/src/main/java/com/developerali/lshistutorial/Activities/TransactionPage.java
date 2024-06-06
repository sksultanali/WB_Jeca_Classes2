package com.developerali.lshistutorial.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.MainActivity;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityTransactionPageBinding;

import java.util.Date;

public class TransactionPage extends AppCompatActivity {

    ActivityTransactionPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(TransactionPage.this, MainActivity.class);
                i.putExtra("confirm", true);
                startActivity(i);
                finish();
            }
        }, 2500);

        binding.transactionId.setText("LSHIS" + System.currentTimeMillis());
        binding.dateTime.setText(Helper.formatDateTime(new Date().getTime()));


    }
}