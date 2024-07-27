package com.developerali.lshistutorial.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.developerali.lshistutorial.Adapters.QuizListAdapter;
import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.Login;
import com.developerali.lshistutorial.Models.QuizModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityQuizListBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity {

    ActivityQuizListBinding binding;
    String sem;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<QuizModel> arrayList = new ArrayList<>();
    QuizListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

        sem = getIntent().getStringExtra("sem");
        //binding.sem.setText(sem);

        LinearLayoutManager lnm = new LinearLayoutManager(QuizListActivity.this);
        binding.ListRec.setLayoutManager(lnm);
        adapter = new QuizListAdapter(QuizListActivity.this, arrayList);
        binding.ListRec.setAdapter(adapter);

        database.getReference().child("quiz")
                .child(sem)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()){
                                arrayList.clear();
                                for (DataSnapshot data : snapshot.getChildren()){
                                    QuizModel quizModel = data.getValue(QuizModel.class);
                                    if (quizModel != null){
                                        arrayList.add(quizModel);
                                    }
                                }
                                binding.progressBar2.setVisibility(View.GONE);
                                binding.noData.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }else {
                                binding.progressBar2.setVisibility(View.GONE);
                                binding.noData.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.noData.setVisibility(View.VISIBLE);
                        Toast.makeText(QuizListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        if (auth.getCurrentUser() != null){
            String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            database.getReference().child("loggers")
                    .child(auth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String id = snapshot.getValue(String.class);
                                if (!id.equalsIgnoreCase(deviceId)){
                                    auth.signOut();
                                    Helper.showLoginDialog(QuizListActivity.this, "Another Login Detected");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }



    }
}