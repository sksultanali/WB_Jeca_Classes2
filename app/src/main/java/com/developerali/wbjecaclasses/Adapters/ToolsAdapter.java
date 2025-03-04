package com.developerali.wbjecaclasses.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developerali.wbjecaclasses.Models.ToolsModel;
import com.developerali.wbjecaclasses.R;
import com.developerali.wbjecaclasses.databinding.SampleToolsBinding;

import java.util.ArrayList;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.ViewHolder>{

    Activity activity;
    ArrayList<ToolsModel> arrayList = new ArrayList<>();

    public ToolsAdapter(Activity activity, ArrayList<ToolsModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.sample_tools,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolsModel toolsModel = arrayList.get(position);

        holder.binding.toolImg.setImageDrawable(toolsModel.getDrawable());
        holder.binding.toolName.setText(toolsModel.getName());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SampleToolsBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleToolsBinding.bind(itemView);
        }
    }
}
