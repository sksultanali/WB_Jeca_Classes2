package com.developerali.lshistutorial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.developerali.lshistutorial.databinding.CustomDialogBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Helper {

    public static int COUPON_CODE;
    public static String BLOG_LINK;
    public static int COUPON_DISCOUNT;
    public static String My_Name;
    public static String My_Email;
    public static String My_PROFILE_ID;

    public static int SELLING_PRICE;
    public static int NORMAL_PRICE;
    public static int DISCOUNT;
    public static String formatDate (Long date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL yyyy");
        return simpleDateFormat.format(date);
    }
    public static String formatDateTime (Long date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL yyyy HH:mm a");
        return simpleDateFormat.format(date);
    }

    public static String getDateKey(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhh");
        String date = simpleDateFormat.format(new Date());
        return date;
    }


    public static void changeSem(String id, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", id);
        editor.apply();
    }
    public static String getSem(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Ids", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);
        return id;
    }

    public static String semText(String sem){
        if (sem.equalsIgnoreCase("1ST SEM")){
            return "Sem 1";
        } else if (sem.equalsIgnoreCase("2ND SEM")) {
            return "Sem 2";
        }else if (sem.equalsIgnoreCase("3RD SEM")) {
            return "Sem 3";
        }else if (sem.equalsIgnoreCase("4RTH SEM")) {
            return "Sem 4";
        }else if (sem.equalsIgnoreCase("5TH SEM")) {
            return "Sem 5";
        }else if (sem.equalsIgnoreCase("6TH SEM")) {
            return "Sem 6";
        }else {
            return "NA";
        }
    }

    public static boolean isChromeCustomTabsSupported(@NonNull final Context context) {
        Intent serviceIntent = new Intent("android.support.customtabs.action.CustomTabsService");
        serviceIntent.setPackage("com.android.chrome");
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !resolveInfos.isEmpty();
    }
    public static void openChromeTab(String link, Activity activity){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.main_color));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(link));
    }
    public static boolean isConnectedNetwork (Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public static boolean isLocationEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsEnabled || isNetworkEnabled;
    }

    public static String sanitizeForFirebase(String input) {
        return input.replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("@", "")
                .replace("!", "")
                .replace("*", "")
                .replace(")", "")
                .replace("(", "")
                .replace("[", "")
                .replace("]", "");
    }

    @SuppressLint("ResourceAsColor")
    public static void showLoginDialog(Activity activity, String title) {
        CustomDialogBinding dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(activity));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogBinding.getRoot())
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

        dialogBinding.titleText.setText(title);
        dialogBinding.messageText.setText("Please login to continue this option. We' re waiting for your response!");

        dialogBinding.yesBtnText.setText("Login");

        dialogBinding.noBtn.setVisibility(View.VISIBLE);
        dialogBinding.noBtnText.setText("Later");

        dialogBinding.loginBtn.setOnClickListener(v->{
            activity.startActivity(new Intent(activity.getApplicationContext(), Login.class));
        });

        dialogBinding.noBtn.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();
    }

}
