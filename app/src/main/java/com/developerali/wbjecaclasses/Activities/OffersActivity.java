package com.developerali.wbjecaclasses.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.developerali.wbjecaclasses.Adapters.HomeOfferAdapter;
import com.developerali.wbjecaclasses.Models.CouponModel;
import com.developerali.wbjecaclasses.R;
import com.developerali.wbjecaclasses.databinding.ActivityOffersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OffersActivity extends AppCompatActivity {

    ActivityOffersBinding binding;
    FirebaseDatabase database;
    ArrayList<CouponModel> arrayList = new ArrayList<>();
    HomeOfferAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOffersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

        LinearLayoutManager lnm = new LinearLayoutManager(OffersActivity.this);
        binding.offersRec.setLayoutManager(lnm);
        adapter = new HomeOfferAdapter(OffersActivity.this, arrayList);
        binding.offersRec.setAdapter(adapter);

        binding.progressBar2.setVisibility(View.VISIBLE);
        database.getReference().child("coupons")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            arrayList.clear();
                            for (DataSnapshot coupons : snapshot.getChildren()){
                                CouponModel couponModel = new CouponModel();

                                couponModel.setCode(coupons.child("code").getValue(String.class));
                                couponModel.setPercentage(coupons.child("percentage").getValue(String.class));
                                couponModel.setValid(coupons.child("valid").getValue(Long.class));

                                arrayList.add(couponModel);
                            }

                            adapter.notifyDataSetChanged();
                            binding.noData.setVisibility(View.GONE);
                            binding.progressBar2.setVisibility(View.GONE);

                        }else {
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.noData.setVisibility(View.VISIBLE);
                        Toast.makeText(OffersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });







    }
}