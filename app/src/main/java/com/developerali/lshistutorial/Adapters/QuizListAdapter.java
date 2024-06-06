package com.developerali.lshistutorial.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developerali.lshistutorial.Activities.PlayQuizActivity;
import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.Models.QuizModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ChildQuizListBinding;

import java.util.ArrayList;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder>{

    Activity activity;
    ArrayList<QuizModel> models;

    public QuizListAdapter(Activity activity, ArrayList<QuizModel> models) {
        this.activity = activity;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_quiz_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizModel quizModel = models.get(position);
        holder.binding.quizTitle.setText(quizModel.getTitle());
//        holder.binding.quizDate.setText(Helper.formatDate(quizModel.getDate()));
        holder.binding.counting.setText("#" + (position+1));

        holder.binding.playBtn.setOnClickListener(v->{
            Intent i = new Intent(activity.getApplicationContext(), PlayQuizActivity.class);
            i.putExtra("apiKey", quizModel.getApiKey());
            activity.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildQuizListBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildQuizListBinding.bind(itemView);
        }
    }
}
