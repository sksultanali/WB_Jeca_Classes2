package com.developerali.lshistutorial.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.Login;
import com.developerali.lshistutorial.MainActivity;
import com.developerali.lshistutorial.Models.BooksModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityDetailedBinding;
import com.developerali.lshistutorial.databinding.CustomDialogBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {

    ActivityDetailedBinding binding;
    FirebaseDatabase database;
    String id;
    FirebaseAuth auth;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = DetailedActivity.this;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            id = extractLink(data);
        }else {
            id = getIntent().getStringExtra("id");
        }

        if (id != null){
            getBooks(id);
        }else {

        }

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

        binding.whatsapp.setOnClickListener(v->{
            if (auth.getCurrentUser() == null){
                showLoginDialog();
            }else {
                try {
                    String message = "Hi, I "+ Helper.My_Name +", found a book on LS His Tutorial. And have some query. Should we talk now? \n\n"+
                            "Book Link- https://www.goldenheight.in/" + id;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + "8967254087" + "&text=" + message));
                    startActivity(i);
                }catch (Exception e){
                    Helper.showLoginDialog(DetailedActivity.this, "Only Whatsapp",
                            "We are available only on whatsapp now. Please make sure you have installed whatsapp to conntact us.\nThank you..!");
                }
            }
        });

        binding.share.setOnClickListener(v->{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "This book is awesome. For best deal buy this now ! (using app only) \n\n" +
                            " link: https://www.goldenheight.in/" + id);
            if (sharingIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
            }
        });

        binding.buyBtn.setOnClickListener(v->{
            if (auth.getCurrentUser() != null){
                Intent i = new Intent(DetailedActivity.this, CheckOut.class);
                i.putExtra("id", id);
                startActivity(i);
            }else {
                showLoginDialog();
            }
        });


    }

    @SuppressLint("ResourceAsColor")
    public void showLoginDialog() {
        CustomDialogBinding dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(DetailedActivity.this));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogBinding.getRoot())
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

        dialogBinding.titleText.setText("Login Required");
        dialogBinding.messageText.setText("Please login to continue this option. We' re waiting for your response!");

        dialogBinding.yesBtnText.setText("Login");

        dialogBinding.noBtn.setVisibility(View.VISIBLE);
        dialogBinding.noBtnText.setText("Later");

        dialogBinding.loginBtn.setOnClickListener(v->{
            startActivity(new Intent(DetailedActivity.this, Login.class));
        });

        dialogBinding.noBtn.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();
    }
    private void getBooks(String id) {
        DatabaseReference reference = database.getReference().child("books")
                .child(Helper.getSem(DetailedActivity.this)).child(id);
        //Query query = reference.orderByChild("sem").equalTo(Helper.getSem(MainActivity.this));

        binding.progressBar.setVisibility(View.VISIBLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot books) {
                if (books.exists()){

                    BooksModel model = new BooksModel();
                    model.setId(books.getKey());
                    model.setTitle(books.child("title").getValue(String.class));
                    model.setDescription(books.child("description").getValue(String.class));
                    model.setImages(model.getImages());
                    //model.setFileUrl(books.child("fileUrl").getValue(String.class));

                    model.setSellingPrice(books.child("sellingPrice").getValue(Long.class));
                    model.setNormalPrice(books.child("normalPrice").getValue(Long.class));

                    model.setSem(books.child("sem").getValue(String.class));
                    model.setType(books.child("type").getValue(String.class));

                    final ArrayList<String> slideModels = new ArrayList<>();
                    for (DataSnapshot images : books.child("images").getChildren()){
                        slideModels.add(images.getValue(String.class));
                    }
                    model.setImageString(slideModels);

                    setData(model);

                    binding.noData.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.nestedScrollLayout.setVisibility(View.VISIBLE);
                }else {
                    binding.noData.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.nestedScrollLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.noData.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.nestedScrollLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setData(BooksModel model) {
        binding.description.setText(Html.fromHtml(model.getDescription()));
        binding.bookName.setText(model.getTitle());
        if (model.getImageString().get(0) != null && !activity.isDestroyed()){
            Glide.with(activity)
                    .load(model.getImageString().get(0))
                    .placeholder(getDrawable(R.drawable.book_placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.imageBook);
        }

        binding.normalPrice.setPaintFlags(binding.normalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.normalRate.setPaintFlags(binding.normalRate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        binding.selling.setText(model.getSellingPrice()+" /- only");
        binding.sellingPrice.setText(model.getSellingPrice()+"");
        binding.normalRate.setText("₹"+model.getNormalPrice());
        binding.CouponNormal.setText(" | No Coupons Applied");

        binding.normalPrice.setText("₹"+model.getNormalPrice()+"");

        int a = Math.toIntExact(model.getNormalPrice() - model.getSellingPrice());
        int b = (int) a * 100;
        int c = Math.toIntExact((int) b / model.getNormalPrice());

        Helper.SELLING_PRICE = Math.toIntExact(model.getSellingPrice());
        Helper.NORMAL_PRICE = Math.toIntExact(model.getNormalPrice());
        Helper.DISCOUNT = a;

        binding.discount.setText(c + "% OFF");

        binding.sem.setText(Helper.semText(model.getSem()));
    }

    public String extractLink (Uri link){

        List<String> pathSegments = link.getPathSegments();

        if (pathSegments.size() >= 1){
            String value = pathSegments.get(0);
            return value;
        }
        return "noResult";
    }
}