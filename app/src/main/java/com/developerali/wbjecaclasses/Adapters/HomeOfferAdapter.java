package com.developerali.wbjecaclasses.Adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developerali.wbjecaclasses.Helper;
import com.developerali.wbjecaclasses.Models.CouponModel;
import com.developerali.wbjecaclasses.R;
import com.developerali.wbjecaclasses.databinding.ChildCouponsBinding;

import java.util.ArrayList;

public class HomeOfferAdapter extends RecyclerView.Adapter<HomeOfferAdapter.ModelsViewholder>{
    Activity activity;
    ArrayList<CouponModel> models;

    public HomeOfferAdapter(Activity activity, ArrayList<CouponModel> models){
        this.activity = activity;
        this.models = models;
    }

    @NonNull
    @Override
    public ModelsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_coupons, parent, false);
        return new ModelsViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelsViewholder holder, int position) {
        CouponModel offerModels = models.get(position);

        holder.binding.percentage.setText("Get " + offerModels.getPercentage() + "% Discount on Buying");
        holder.binding.code.setText(offerModels.getCode().toUpperCase());
        holder.binding.validity.setText("# Offer ends on "+ Helper.formatDate(offerModels.getValid()));


        holder.binding.code.setOnClickListener(v->{
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Code Copied", offerModels.getCode());
            Toast.makeText(activity, "Copied Clipboard", Toast.LENGTH_LONG).show();
            clipboard.setPrimaryClip(clip);
        });


    }


    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ModelsViewholder extends RecyclerView.ViewHolder{
        ChildCouponsBinding binding;
        public ModelsViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ChildCouponsBinding.bind(itemView);
        }
    }
}
