package com.developerali.lshistutorial.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.developerali.lshistutorial.Models.NotificationModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.SampleNotificationBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder>{

    Context context;
    ArrayList<NotificationModel> models;
    FirebaseDatabase database;
    FirebaseAuth auth;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> models) {
        this.context = context;
        this.models = models;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_notification, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel notificationModel = models.get(position);

        if (notificationModel.getSeen()){
            holder.binding.backgroundNotification.setBackgroundColor(context.getColor(R.color.gray));
        }else {
            holder.binding.backgroundNotification.setBackgroundColor(context.getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(v->{
            holder.binding.backgroundNotification.setBackgroundColor(context.getColor(R.color.gray));
            database.getReference().child("notification")
                    .child(auth.getCurrentUser().getUid())
                    .child(notificationModel.getNotificationId())
                    .child("seen")
                    .setValue(true);
            Toast.makeText(context, "marked as read", Toast.LENGTH_SHORT).show();
        });

        String timeAgo = TimeAgo.using(notificationModel.getNotifyAt());
        String formattedText = "<b>LS His Tutorial</b> • " + timeAgo + " • " + notificationModel.getType();
        CharSequence styledText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY);
        holder.binding.notificationType.setText(styledText);

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        SampleNotificationBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleNotificationBinding.bind(itemView);
        }
    }
}
