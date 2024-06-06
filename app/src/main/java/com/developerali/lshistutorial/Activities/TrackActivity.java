package com.developerali.lshistutorial.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.developerali.lshistutorial.Adapters.BooksAdapter;
import com.developerali.lshistutorial.Adapters.PaperBackAdapter;
import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.Models.PaperBackModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityTrackBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityTrackBinding binding;
    FirebaseAuth auth;
    PaperBackAdapter booksAdapter;
    ArrayList<PaperBackModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        LinearLayoutManager lnm = new LinearLayoutManager(TrackActivity.this);
        binding.books.setLayoutManager(lnm);
        booksAdapter = new PaperBackAdapter(TrackActivity.this, arrayList);
        binding.books.setAdapter(booksAdapter);

        database.getReference().child("paperBack")
                .child(Helper.getSem(TrackActivity.this))
                .child(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            arrayList.clear();
                            for (DataSnapshot snapshot1: snapshot.getChildren()){
                                PaperBackModel model = new PaperBackModel();
                                model.setId(snapshot1.getKey());
                                model.setLog(snapshot1.child("log").getValue(String.class));
                                if (snapshot1.child("status").exists()){
                                    model.setStatus(snapshot1.child("status").getValue(String.class));
                                }
                                if (snapshot1.child("bookId").exists()){
                                    model.setBookId(snapshot1.child("bookId").getValue(String.class));
                                }
                                if (snapshot1.child("consId").exists()){
                                    model.setConsId(snapshot1.child("consId").getValue(String.class));
                                }

                                arrayList.add(model);
                            }
                            binding.progressBar5.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.GONE);
                            booksAdapter.notifyDataSetChanged();
                        }else {
                            binding.progressBar5.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar5.setVisibility(View.GONE);
                        binding.noData.setVisibility(View.VISIBLE);
                    }
                });

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });


    }
}