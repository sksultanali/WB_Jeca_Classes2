package com.developerali.wbjecaclasses.Adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.developerali.wbjecaclasses.Helper;
import com.developerali.wbjecaclasses.Models.PaperBackModel;
import com.developerali.wbjecaclasses.R;
import com.developerali.wbjecaclasses.databinding.ChildPaperBackBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaperBackAdapter extends RecyclerView.Adapter<PaperBackAdapter.ViewHolder>{

    Activity activity;
    ArrayList<PaperBackModel> models;
    FirebaseDatabase database;

    public PaperBackAdapter(Activity activity, ArrayList<PaperBackModel> models) {
        this.activity = activity;
        this.models = models;
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_paper_back, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaperBackModel paperBackModel = models.get(position);

        holder.binding.sem.setText(Helper.getSem(activity));
        holder.binding.bookTitle.setText(paperBackModel.getLog());
        if (paperBackModel.getConsId() != null){
            holder.binding.consId.setText(paperBackModel.getConsId());
        }

        if (paperBackModel.getConsId() != null){
            holder.binding.consId.setText(paperBackModel.getConsId());
        }

        if (paperBackModel.getStatus() != null){
            holder.binding.type.setText(paperBackModel.getStatus());
        }else {
            holder.binding.type.setText("Failed");
        }

        if (paperBackModel.getBookId() != null && !paperBackModel.getBookId().equalsIgnoreCase("allBooks")){
            database.getReference().child("books")
                    .child(Helper.getSem(activity))
                    .child(paperBackModel.getBookId())
                    .child("images")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot3) {
                            if (snapshot3.exists()){
                                final ArrayList<SlideModel> slideModels = new ArrayList<>();
                                for (DataSnapshot snapshot2: snapshot3.getChildren()){
                                    slideModels.add(new SlideModel(snapshot2.getValue(String.class), ScaleTypes.CENTER_CROP));
                                }
                                holder.binding.imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else if (paperBackModel.getBookId() != null && paperBackModel.getBookId().equalsIgnoreCase("allBooks")){
            final ArrayList<SlideModel> slideModels = new ArrayList<>();
            slideModels.add(new SlideModel("https://i.postimg.cc/L6msstwy/LS-His-Tutorial.png", ScaleTypes.CENTER_CROP));
            holder.binding.imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
        }


        holder.binding.consId.setOnClickListener(v->{
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied", paperBackModel.getConsId());
            Toast.makeText(activity, "Copied Clipboard", Toast.LENGTH_LONG).show();
            clipboard.setPrimaryClip(clip);
        });


        holder.itemView.setOnClickListener(v->{
            if (Helper.isChromeCustomTabsSupported(activity)){
                Helper.openChromeTab("https://www.indiapost.gov.in/_layouts/15/dop.portal.tracking/trackconsignment.aspx",
                        activity);
            }else {
                String url6 = "https://www.indiapost.gov.in/_layouts/15/dop.portal.tracking/trackconsignment.aspx";
                Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse(url6));
                activity.startActivity(intent6);
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildPaperBackBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildPaperBackBinding.bind(itemView);
        }
    }
}
