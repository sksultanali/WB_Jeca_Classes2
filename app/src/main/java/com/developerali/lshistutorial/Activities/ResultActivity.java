package com.developerali.lshistutorial.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        int score = getIntent().getIntExtra("correct", 0);
        int total = getIntent().getIntExtra("total", 0);

        int earnedCoin = score * 10;

        binding.score.setText(String.valueOf(score) + "/" + String.valueOf(total));
        binding.earnedCoins.setText(String.valueOf(earnedCoin));


        binding.shareMyScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = readyText(String.valueOf(earnedCoin));
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                if (sharingIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(sharingIntent,"Share Quiz Score"));
                }
            }
        });

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });


    }

    public String readyText(String Reward){

        String message ="আস্সালামুআলাইকুম,\n" + "\n" +
                "My Life Qur'an পরিচালিত *কুইজ প্রতিযোগিতায়* অংশগ্রহণ করেছিলাম। আলহামদুলিল্লাহ সফল হয়েছি। এই কুইজ থেকে *" + Reward+" Reward* পেয়েছি।\n" +
                "\n" +
                "আমার মতো তুমিও *কুইজ খেলে এবং হাদিস কোরান পড়ে* অনেক পুরস্কার ও reward পেতে পারো....\n" +
                "\n" +
                "এক্ষুনি My Life Qura'n app ডাউনলোড করো। \n" +

                "----------------------------\n" + "\n" +
                "       Presented By\n" +
                "       *My Life Qur'an*\n" +
                "\n" +
                "Download App From PlayStore\n"+
                "https://play.google.com/store/apps/details?id=com.developerali.mylifequran";
        return message;
    }
}