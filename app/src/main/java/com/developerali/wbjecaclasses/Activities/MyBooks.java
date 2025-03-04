package com.developerali.wbjecaclasses.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.developerali.wbjecaclasses.Adapters.BooksAdapter;
import com.developerali.wbjecaclasses.Helper;
import com.developerali.wbjecaclasses.Models.BooksModel;
import com.developerali.wbjecaclasses.R;
import com.developerali.wbjecaclasses.databinding.ActivityMyBooksBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBooks extends AppCompatActivity {

    ActivityMyBooksBinding binding;
    ArrayList<BooksModel> booksArray = new ArrayList<>();
    BooksAdapter booksAdapter;
    FirebaseDatabase data;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        data = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        LinearLayoutManager lnm = new LinearLayoutManager(MyBooks.this);
        binding.books.setLayoutManager(lnm);
        booksAdapter = new BooksAdapter(MyBooks.this, booksArray);
        binding.books.setAdapter(booksAdapter);

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

        getBooks();


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
                                    Helper.showLoginDialog(MyBooks.this, "Another Login Detected");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void getBooks() {
        DatabaseReference reference = data.getReference().child("books")
                .child(Helper.getSem(MyBooks.this));
        //Query query = reference.orderByChild("sem").equalTo(Helper.getSem(MainActivity.this));
        booksArray.clear();
        binding.progressBar5.setVisibility(View.VISIBLE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    booksArray.clear();
                    for (DataSnapshot books : snapshot.getChildren()){
                        if (books.child("buyers").child(auth.getCurrentUser().getUid()).exists()){
                            BooksModel model = new BooksModel();
                            model.setId(books.getKey());
                            model.setTitle(books.child("title").getValue(String.class));
                            model.setDescription(books.child("description").getValue(String.class));
                            model.setImages(model.getImages());

                            model.setFileUrl(books.child("fileUrl").getValue(String.class));

                            model.setSellingPrice(books.child("sellingPrice").getValue(Long.class));
                            model.setNormalPrice(books.child("normalPrice").getValue(Long.class));

                            model.setSem(books.child("sem").getValue(String.class));
                            model.setType(books.child("type").getValue(String.class));

                                final ArrayList<SlideModel> slideModels = new ArrayList<>();
                                for (DataSnapshot images : books.child("images").getChildren()){
                                    slideModels.add(new SlideModel(images.getValue(String.class), ScaleTypes.CENTER_CROP));
                                }
                            model.setImages(slideModels);
                            model.setReadBook(true);

                            booksArray.add(model);
                        }
                    }

                    booksAdapter.notifyDataSetChanged();
                    binding.noData.setVisibility(View.GONE);

                    if (booksArray.isEmpty()){
                        binding.noData.setVisibility(View.VISIBLE);
                    }else {
                        binding.noData.setVisibility(View.GONE);
                    }

                }else {
                    binding.noData.setVisibility(View.VISIBLE);
                    booksAdapter.notifyDataSetChanged();
                }

                binding.progressBar5.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.noData.setVisibility(View.VISIBLE);
                binding.progressBar5.setVisibility(View.GONE);
            }
        });
    }
}