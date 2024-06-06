package com.developerali.lshistutorial.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.developerali.lshistutorial.Activities.DetailedActivity;
import com.developerali.lshistutorial.Activities.ReadBook;
import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.Models.BooksModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ChildBookBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import io.grpc.okhttp.internal.framed.Header;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder>{

    Activity activity;
    ArrayList<BooksModel> models;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public BooksAdapter(Activity activity, ArrayList<BooksModel> models) {
        this.activity = activity;
        this.models = models;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BooksModel booksModel = models.get(position);

        holder.binding.bookTitle.setText(booksModel.getTitle());
        holder.binding.sellingPrice.setText(booksModel.getSellingPrice()+"");
        holder.binding.normalPrice.setText("₹"+booksModel.getNormalPrice());
        holder.binding.sem.setText(Helper.semText(booksModel.getSem()));

        holder.binding.normalPrice.setPaintFlags(holder.binding.normalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int a = Math.toIntExact(booksModel.getNormalPrice() - booksModel.getSellingPrice());
        int b = (int) a * 100;
        int c = Math.toIntExact((int) b / booksModel.getNormalPrice());

        holder.binding.discount.setText(c + "% OFF");
        holder.binding.imageSlider.setImageList(booksModel.getImages(), ScaleTypes.CENTER_CROP);
        holder.binding.type.setText(booksModel.getType());
        holder.binding.type.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.blink));



        holder.binding.readNowBtn.setOnClickListener(v->{
            Intent i = new Intent(activity.getApplicationContext(), ReadBook.class);
            i.putExtra("url", booksModel.getFileUrl());
            activity.startActivity(i);
        });

        holder.itemView.setOnClickListener(v->{
            Intent i = new Intent(activity.getApplicationContext(), DetailedActivity.class);
            i.putExtra("id", booksModel.getId());
            activity.startActivity(i);
        });


        if (booksModel.getReadBook() != null){
            if (booksModel.getReadBook()){
                holder.itemView.setEnabled(false);
                holder.binding.priceLayout.setVisibility(View.GONE);
                holder.binding.readNowBtn.setVisibility(View.VISIBLE);
            }
        }else if (booksModel.getType().equalsIgnoreCase("Free")){

            holder.itemView.setEnabled(false);
            holder.binding.priceLayout.setVisibility(View.GONE);
            holder.binding.readNowBtn.setVisibility(View.VISIBLE);

        }else if (booksModel.getType().equalsIgnoreCase("Paid")){

            holder.itemView.setEnabled(true);
            holder.binding.priceLayout.setVisibility(View.VISIBLE);
            holder.binding.readNowBtn.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildBookBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildBookBinding.bind(itemView);
        }
    }
}
