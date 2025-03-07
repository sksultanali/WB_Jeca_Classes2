package com.developerali.wbjecaclasses.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.developerali.wbjecaclasses.Adapters.NotificationAdapter;
import com.developerali.wbjecaclasses.Models.NotificationModel;
import com.developerali.wbjecaclasses.R;
import com.developerali.wbjecaclasses.databinding.ActivityNotificationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;
    FirebaseDatabase database;
    NotificationAdapter adapter;
    ArrayList<NotificationModel> arrayList = new ArrayList<>();
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

        LinearLayoutManager lnm = new LinearLayoutManager(NotificationActivity.this);
        binding.offersRec.setLayoutManager(lnm);
        adapter = new NotificationAdapter(NotificationActivity.this, arrayList);
        binding.offersRec.setAdapter(adapter);

        binding.progressBar2.setVisibility(View.VISIBLE);
        database.getReference().child("notification")
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            arrayList.clear();
                            for (DataSnapshot notify : snapshot.getChildren()){
                                NotificationModel notificationModel = notify.getValue(NotificationModel.class);
                                notificationModel.setNotificationId(notify.getKey());
                                arrayList.add(notificationModel);
                            }
                            Collections.reverse(arrayList);
                            adapter.notifyDataSetChanged();
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.GONE);

                            if (arrayList.isEmpty()){
                                binding.clear.setVisibility(View.GONE);
                            }else {
                                binding.clear.setVisibility(View.VISIBLE);
                            }

                        }else {
                            binding.clear.setVisibility(View.GONE);
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.noData.setVisibility(View.VISIBLE);
                        Toast.makeText(NotificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.clear.setOnClickListener(v->{
            database.getReference().child("notification")
                    .child(auth.getCurrentUser().getUid())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            arrayList.clear();
                            adapter.notifyDataSetChanged();
                            binding.noData.setVisibility(View.VISIBLE);
                            Toast.makeText(NotificationActivity.this, "cleared all", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}