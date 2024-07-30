package com.developerali.lshistutorial.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.Login;
import com.developerali.lshistutorial.MainActivity;
import com.developerali.lshistutorial.Models.ToolsModel;
import com.developerali.lshistutorial.Models.UserModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityProfileBinding;
import com.developerali.lshistutorial.databinding.CustomDialogBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.Help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    ArrayList<ToolsModel> arrayList = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    FirebaseStorage storage;
    Activity activity;
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        activity = ProfileActivity.this;

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

        arrayList.clear();
        //arrayList.add(new ToolsModel("Add Money", getDrawable(R.drawable.wallet)));
        arrayList.add(new ToolsModel("My Books", getDrawable(R.drawable.digital_library)));
        arrayList.add(new ToolsModel("Question", getDrawable(R.drawable.question)));
        arrayList.add(new ToolsModel("Play Quiz", getDrawable(R.drawable.quiz_logo)));
        arrayList.add(new ToolsModel("Read Blog", getDrawable(R.drawable.blog)));
        arrayList.add(new ToolsModel("Best Offer", getDrawable(R.drawable.coupon)));
        arrayList.add(new ToolsModel("YouTube", getDrawable(R.drawable.yout)));
        arrayList.add(new ToolsModel("My Orders", getDrawable(R.drawable.delivery)));
        arrayList.add(new ToolsModel("Policy", getDrawable(R.drawable.privacy)));
        arrayList.add(new ToolsModel("Terms ", getDrawable(R.drawable.terms)));
        arrayList.add(new ToolsModel("Refund ", getDrawable(R.drawable.cashback)));
        arrayList.add(new ToolsModel("Developer ", getDrawable(R.drawable.developer_mode_24)));


        binding.sem.setText(Helper.getSem(ProfileActivity.this));

        myListAdapter adapter = new myListAdapter();
        binding.toolsList.setAdapter(adapter);

        binding.toolsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        if (auth.getCurrentUser() != null){
                            startActivity(new Intent(ProfileActivity.this, MyBooks.class));
                        }else {
                            showLoginDialog();
                        }
                        break;
                    case 1:
                        Helper.showLoginDialog(ProfileActivity.this, "Not Eligible",
                                "Your profile is not eligible for asking question for now. We will notify you soon.\nThank you..!");
                        break;
                    case 2:
                        Intent n = new Intent(ProfileActivity.this, QuizListActivity.class);
                        n.putExtra("sem", binding.sem.getText().toString());
                        startActivity(n);
                        break;
                    case 3:
                        if (Helper.BLOG_LINK == null){
                            Toast.makeText(ProfileActivity.this, "Error 404!", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent t = new Intent(ProfileActivity.this, WebView.class);
                            t.putExtra("share", Helper.BLOG_LINK);
                            startActivity(t);
                        }
                        break;
                    case 4:
                        Intent o = new Intent(ProfileActivity.this, OffersActivity.class);
                        startActivity(o);
                        break;
                    case 5:
                        if (Helper.isChromeCustomTabsSupported(ProfileActivity.this)){
                            Helper.openChromeTab("https://youtube.com/@lshistutorial8766?si=H7p9Tv0is32gFDZb", ProfileActivity.this);
                        }else {
                            String url6 = "https://youtube.com/@lshistutorial8766?si=H7p9Tv0is32gFDZb";
                            Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse(url6));
                            startActivity(intent6);
                        }
                        break;
                    case 6:
                        if (auth.getCurrentUser() != null){
                            startActivity(new Intent(ProfileActivity.this, TrackActivity.class));
                        }else {
                            showLoginDialog();
                        }
                        break;
                    case 7:
                        if (Helper.isChromeCustomTabsSupported(ProfileActivity.this)){
                            Helper.openChromeTab("https://lshistutorial.blogspot.com/p/ls-his-tutorial-privacy-policy.html", ProfileActivity.this);
                        }else {
                            String url6 = "https://lshistutorial.blogspot.com/p/ls-his-tutorial-privacy-policy.html";
                            Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse(url6));
                            startActivity(intent6);
                        }
                        break;
                    case 8:
                        if (Helper.isChromeCustomTabsSupported(ProfileActivity.this)){
                            Helper.openChromeTab("https://missionlearningbangla.blogspot.com/p/terms-conditions_18.html", ProfileActivity.this);
                        }else {
                            String url6 = "https://missionlearningbangla.blogspot.com/p/terms-conditions_18.html";
                            Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse(url6));
                            startActivity(intent6);
                        }
                        break;
                    case 9:
                        if (Helper.isChromeCustomTabsSupported(ProfileActivity.this)){
                            Helper.openChromeTab("https://missionlearningbangla.blogspot.com/p/refund-policy.html", ProfileActivity.this);
                        }else {
                            String url6 = "https://missionlearningbangla.blogspot.com/p/refund-policy.html";
                            Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse(url6));
                            startActivity(intent6);
                        }
                        break;
                    case 10:
                       try {
                           String message = "Hi, I have seen the LS His Tutorial app and have some query. Should we talk now? ";
                           Intent i = new Intent(Intent.ACTION_VIEW);
                           i.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + "8967254087" + "&text=" + message));
                           startActivity(i);
                       }catch (Exception e){
                           Helper.showLoginDialog(ProfileActivity.this, "Only Whatsapp",
                                   "We are available only on whatsapp now. Please make sure you have installed whatsapp to conntact us.\nThank you..!");
                       }
                        break;
                }
            }
        });

        if (auth.getCurrentUser() != null){
            binding.login.setVisibility(View.GONE);
            binding.logOut.setVisibility(View.VISIBLE);

            fireStore.collection("users")
                    .document(auth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){

                                UserModel user = new UserModel();

                                String Name = documentSnapshot.getString("name");
                                String Email = documentSnapshot.getString("email");
                                binding.discoverProfileName.setText(Name);
                                binding.profileMail.setText(Email);

                                if (documentSnapshot.getString("imageUrl") != null) {
                                    user.setImageUrl(documentSnapshot.getString("imageUrl"));

                                    if (user.getImageUrl() != null && !activity.isDestroyed()){
                                        Glide.with(activity)
                                                .load(user.getImageUrl())
                                                .placeholder(getDrawable(R.drawable.logo))
                                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                .into(binding.profImage);
                                    }
                                }

                                if (documentSnapshot.getLong("balance")!= null) {
                                    user.setBalance(documentSnapshot.getLong("balance"));
                                    binding.money.setText(""+user.getBalance());
                                }

                                binding.profileSegment.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            binding.profImage.setOnClickListener(v->{
                ImagePicker.with(this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .start(85);
            });
        }else {
            binding.login.setVisibility(View.VISIBLE);
            binding.logOut.setVisibility(View.GONE);
        }

        binding.login.setOnClickListener(v->{
            startActivity(new Intent(ProfileActivity.this, Login.class));
        });

        binding.logOut.setOnClickListener(v->{
            auth.signOut();
            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(i);
            Snackbar snackbar = Snackbar.make(binding.linearLayout2, "Logout Successful...", Snackbar.LENGTH_LONG)
                    .setAction("Login Now", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(ProfileActivity.this, Login.class);
                            startActivity(i);
                            finish();
                        }
                    });
            snackbar.show();
            finish();
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 85) {
            //Image Uri will not be null for RESULT_OK
            Uri uri = data.getData();
            binding.profImage.setImageURI(uri);

            progressDialog.setMessage("uploading profile image...");
            progressDialog.show();
            StorageReference storageReference = storage.getReference().child("profiles")
                    .child(auth.getCurrentUser().getUid());
            storageReference
                    .putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        UploadBanner(imageUrl);
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    private void UploadBanner(String imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("fieldName", imageUrl);

        progressDialog.setMessage("image uploading process");
        progressDialog.show();

        fireStore.collection("users").document(auth.getCurrentUser().getUid())
                .update("imageUrl", imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this, "image Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @SuppressLint("ResourceAsColor")
    public void showLoginDialog() {
        CustomDialogBinding dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(ProfileActivity.this));

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
            startActivity(new Intent(ProfileActivity.this, Login.class));
        });

        dialogBinding.noBtn.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();
    }

    public class myListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater obj = getLayoutInflater();
            View view1 = obj.inflate(R.layout.sample_tools, null);
            ImageView imageView = view1.findViewById(R.id.toolImg);
            TextView textView = view1.findViewById(R.id.toolName);

            ToolsModel toolsModel = arrayList.get(i);
            textView.setText(toolsModel.getName());
            imageView.setImageDrawable(toolsModel.getDrawable());

            return view1;
        }
    }
}