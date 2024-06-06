package com.developerali.lshistutorial.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.FeatureInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.Login;
import com.developerali.lshistutorial.Models.NotificationModel;
import com.developerali.lshistutorial.Models.TransactionsModel;
import com.developerali.lshistutorial.Models.UserModel;
import com.developerali.lshistutorial.Notification.FcmNotificationsSender;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityCheckOutBinding;
import com.developerali.lshistutorial.databinding.AddressDialogBoxBinding;
import com.developerali.lshistutorial.databinding.CustomDialogBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckOut extends AppCompatActivity implements PaymentResultWithDataListener, ExternalWalletListener {

    ActivityCheckOutBinding binding;
    FirebaseDatabase database;
    int sellingPrice, normalPrice, paperbackPrice,
            ebookPrice, deliveryPrice, allHardPrice, extraPrice;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    Activity activity;
    UserModel user;
    String id, fcmKey, dateTime;
    ProgressDialog progressDialog, addDialog;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        activity = CheckOut.this;

        progressDialog = new ProgressDialog(CheckOut.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please do not close app...!");
        simpleDateFormat = new SimpleDateFormat("dd LLL yyyy | hh:mm a");

        addDialog = new ProgressDialog(CheckOut.this);
        addDialog.setMessage("saving data");
        addDialog.setCancelable(false);
        addDialog.setCanceledOnTouchOutside(false);

        id = getIntent().getStringExtra("id");
        getPriceData();

        Helper.COUPON_CODE = 0;
        Helper.COUPON_DISCOUNT = 0;
        deliveryPrice = 0;
        allHardPrice = 0;
        extraPrice = 0;

        binding.normalRate.setPaintFlags(binding.normalRate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        binding.removeCuppon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.appliedCoupon.setVisibility(View.GONE);
                binding.PerDis.setText("");
                binding.ccEdText.setText("");
                binding.applyCode.setEnabled(true);
                getPriceData();

                sellingPrice = ebookPrice;
                deliveryPrice = 0;
                binding.deliverySegment.setVisibility(View.GONE);
                binding.allBookSegment.setVisibility(View.GONE);
                binding.no.setChecked(true);
                binding.ebook.setChecked(true);

                Helper.COUPON_CODE = 0;
                Helper.COUPON_DISCOUNT = 0;
                CalculateDiscount();
            }
        });

        binding.applyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String CC = binding.ccEdText.getText().toString();
                if (CC.isEmpty()){
                    binding.ccEdText.setError("Required!");
                }else {
                    database.getReference().child("coupons")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot coupons : snapshot.getChildren()){
                                            if (coupons.child("code").getValue(String.class).equalsIgnoreCase(CC)){
                                                binding.appliedCoupon.setVisibility(View.VISIBLE);
                                                Helper.COUPON_CODE = Integer.parseInt(coupons.child("percentage").getValue(String.class));
                                                binding.PerDis.setText(Helper.COUPON_CODE + "% Off Applied");
                                                CalculateDiscount();
                                                binding.applyCode.setEnabled(false);

                                                binding.congratulation.playAnimation();
                                                return;
                                            }
                                        }
                                        if (Helper.COUPON_CODE == 0){
                                            Toast.makeText(CheckOut.this, "Coupon code not valid", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                    hideKeyboard();
                }
            }
        });

        fireStore.collection("key")
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

        fireStore.collection("users")
                .document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){

                            user = new UserModel();

                            String Name = documentSnapshot.getString("name");
                            String Email = documentSnapshot.getString("email");
                            binding.discoverProfileName.setText(Name);
                            binding.profileMail.setText(Email);

                            user.setName(Name);
                            user.setEmail(Email);

                            if (documentSnapshot.getString("imageUrl") != null) {
                                user.setImageUrl(documentSnapshot.getString("imageUrl"));

                                if (user.getImageUrl() != null && !activity.isDestroyed()){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        Glide.with(activity)
                                                .load(user.getImageUrl())
                                                .placeholder(getDrawable(R.drawable.logo))
                                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                .into(binding.profImage);
                                    }
                                }
                            }

                            if (documentSnapshot.getLong("balance")!= null) {
                                user.setBalance(documentSnapshot.getLong("balance"));
                            }else {
                               user.setBalance(0L);
                            }

                            if (documentSnapshot.contains("address")) {
                                user.setAddress(documentSnapshot.getString("address"));
                                if (user.getAddress() != null){
                                    binding.profileAddress.setText(user.getAddress());
                                    binding.profileAddress.setVisibility(View.VISIBLE);
                                }else {
                                    binding.profileAddress.setVisibility(View.GONE);
                                }
                            } else {
                                binding.profileAddress.setVisibility(View.GONE);
                            }

                            binding.money.setText(""+user.getBalance());
                            binding.payNow.setOnClickListener(v->{
                                int price = Integer.parseInt(binding.totalAmount.getText().toString());

                                if (binding.paperback.isChecked()){
                                    dateTime = simpleDateFormat.format(new Date());
                                    database.getReference().child("paperBack")
                                            .child(Helper.getSem(CheckOut.this))
                                            .child(auth.getCurrentUser().getUid())
                                            .child(Helper.getDateKey())
                                            .child("log").setValue("Payment Open "+ price + "/-  | " + dateTime);
                                    if (user.getAddress() == null){
                                        openAddressBox();
                                    }else {
                                        startPayment(price);
                                    }
                                }else {
                                    startPayment(price);
                                }

                            });

                            binding.profileSegment.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        Checkout.preload(getApplicationContext());

        binding.bookType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ebook) {
                    sellingPrice = ebookPrice;
                    deliveryPrice = 0;
                    binding.deliverySegment.setVisibility(View.GONE);
                    binding.allBookSegment.setVisibility(View.GONE);
                    binding.no.setChecked(true);
                } else if (checkedId == R.id.paperback) {
                    sellingPrice = paperbackPrice;
                    deliveryPrice = 65;
                    binding.deliverySegment.setVisibility(View.VISIBLE);
                    binding.allBookSegment.setVisibility(View.VISIBLE);
                }
                extraPrice = 0;
                CalculateDiscount();
                setPriceData();
            }
        });
        binding.allBookWant.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yes) {
                    sellingPrice = allHardPrice;
                    extraPrice = allHardPrice - normalPrice;
                    deliveryPrice = 0;
                    binding.deliverySegment.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.no) {
                    sellingPrice = paperbackPrice;
                    extraPrice = 0;
                    deliveryPrice = 65;
                    binding.deliverySegment.setVisibility(View.VISIBLE);
                }
                CalculateDiscount();
                setPriceData();
            }
        });

        binding.profileAddress.setOnClickListener(v->{
            openAddressBox();
        });
    }

    private void openAddressBox() {
        AddressDialogBoxBinding addressBinding = AddressDialogBoxBinding.inflate(getLayoutInflater());

        // Create a new dialog and set the custom layout
        Dialog dialog = new Dialog(this);
        dialog.setContentView(addressBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        //addressBinding.nameInput.setText(user.getName());
        addressBinding.phone.postDelayed(new Runnable() {
            @Override
            public void run() {
                addressBinding.phone.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(addressBinding.phone, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);

        addressBinding.postBtn.setOnClickListener(v->{
            //String name = addressBinding.nameInput.getText().toString();
            String phone = addressBinding.phone.getText().toString();
            String address = addressBinding.addressBox.getText().toString();
            String pinCode = addressBinding.pinCode.getText().toString();

            if (phone.isEmpty()){
                addressBinding.phone.setError("*");
            }else if (address.isEmpty()){
                addressBinding.addressBox.setError("*");
            }else if (pinCode.isEmpty()){
                addressBinding.pinCode.setError("*");
            }else {

                String makeAddress =
                        "Address: " + addressBinding.addressBox.getText().toString() + ", " +
                                addressBinding.pinCode.getText().toString() + "\n\n" +
                                "Phone: " + addressBinding.phone.getText().toString();
                user.setAddress(makeAddress);

                binding.profileAddress.setText(makeAddress);
                binding.profileAddress.setVisibility(View.VISIBLE);

                addDialog.show();

                DocumentReference userDocRef = fireStore.collection("users").document(auth.getCurrentUser().getUid());
                Task<Void> updateTask = userDocRef.update("address", makeAddress);
                updateTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addDialog.dismiss();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        addDialog.dismiss();
                        dialog.dismiss();
                    }
                });
            }
        });


        // Show the dialog
        dialog.show();
    }

    private void hideKeyboard() {
        // Get the input method manager
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void startPayment(int normalPrice) {
        normalPrice = normalPrice * 100;
        final Activity activity = this;
        final Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_qxVGEMscsU8vv6");

//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(myReceiver, filter, Context.RECEIVER_NOT_EXPORTED);


        try {
            JSONObject options = new JSONObject();
            options.put("name", "LS His Tutorial");
            options.put("description", "Book Buying Online From LS His Tutorial");
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);

            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://play-lh.googleusercontent.com/j0WtMnU-gIerEpwAJ13ZJQxpXeOXipUeZekBpwvWHTzkyV_wmCYEGVfhq_VaBcq_mg");
            options.put("currency", "INR");
            options.put("amount", normalPrice);
//            options.put("amount", "1000");

            JSONObject preFill = new JSONObject();
            preFill.put("email", user.getEmail());
            preFill.put("contact", "");

            options.put("prefill", preFill);

            checkout.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }


    }
    @SuppressLint("ResourceAsColor")
    public void showNoMoneyDialog() {
        CustomDialogBinding dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(CheckOut.this));

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogBinding.getRoot())
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

        dialogBinding.titleText.setText("Not Enough Wallet Balance");
        dialogBinding.messageText.setText("Please add money to your wallet for completing this order!");

        dialogBinding.yesBtnText.setText("Add Money");

        dialogBinding.noBtn.setVisibility(View.VISIBLE);
        dialogBinding.noBtnText.setText("Later");

        dialogBinding.loginBtn.setOnClickListener(v->{
            startActivity(new Intent(CheckOut.this, AddMoney.class));
            dialog.dismiss();
        });

        dialogBinding.noBtn.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();
    }
    private void getPriceData() {
        database.getReference().child("books")
                .child(Helper.getSem(CheckOut.this))
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            ebookPrice = snapshot.child("sellingPrice").getValue(Integer.class);
                            normalPrice = snapshot.child("normalPrice").getValue(Integer.class);
                            if (snapshot.child("hardPrice").exists()){
                                paperbackPrice = snapshot.child("hardPrice").getValue(Integer.class);
                            }

                            if (paperbackPrice == 0){
                                binding.paperback.setEnabled(false);
                            }else {
                                binding.paperback.setEnabled(true);
                            }

                            sellingPrice = ebookPrice;
                            setPriceData();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference().child("allPrices")
                .child(Helper.getSem(CheckOut.this))
                .child("hardPrice")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            allHardPrice = snapshot.getValue(Integer.class);
                            //binding.yes.setEnabled(true);

                            //Toast.makeText(activity, allHardPrice + " Price", Toast.LENGTH_SHORT).show();

                            if (allHardPrice == 0){
                                binding.paperback.setEnabled(false);
                            }else {
                                binding.paperback.setEnabled(true);
                            }
                            binding.no.setChecked(true);

                        }else {
                            allHardPrice =0;
                            binding.yes.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void setPriceData() {
        binding.basePrice.setText(""+ (normalPrice + extraPrice));
        binding.totalAmount.setText(""+ (sellingPrice + deliveryPrice));
        binding.delivery.setText(""+deliveryPrice);

        binding.discount.setText("- "+ ((normalPrice+extraPrice) - sellingPrice - Helper.COUPON_DISCOUNT));
        binding.coupon.setText("- "+Helper.COUPON_DISCOUNT);

        binding.sellingPrice.setText("₹" + (sellingPrice + deliveryPrice) + " /- Only");
        binding.normalRate.setText("₹" +(normalPrice + extraPrice) );

        if (Helper.COUPON_DISCOUNT == 0){
            binding.CouponNormal.setText(" | Coupon no applied");
        }else {
            binding.CouponNormal.setText(" | Coupon applied");
        }

        if (binding.paperback.isChecked()){
            binding.normalRate.setText("₹" +(sellingPrice + normalPrice));
        }

        binding.coupon.setText(""+Helper.COUPON_DISCOUNT);
    }
    private void CalculateDiscount() {
        int a = sellingPrice * Helper.COUPON_CODE;
        int b = (int) a / 100;

        Helper.COUPON_DISCOUNT = b;
        sellingPrice = sellingPrice - b;
        
        setPriceData();
    }
//    @Override
//    public void onPaymentSuccess(String s) {
//
//
//    }
//    @Override
//    public void onPaymentError(int i, String s) {
//
//    }

    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Toast.makeText(activity, "Payment Success", Toast.LENGTH_LONG).show();

        progressDialog.show();
        String sem = Helper.getSem(CheckOut.this);

        //sending notification via fcm
        if (fcmKey != null){
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/admin",
                    fcmKey,
                    user.getName()+ " Bought a Book",
                    "One of the book of " + sem + " bought by someone. Rs. " +
                            binding.totalAmount.getText().toString() + " /- has been credited to account"
                    , getApplicationContext(), CheckOut.this);
            notificationsSender.SendNotifications();
        }else {

        }

        TransactionsModel transactionsModel = new TransactionsModel();
        transactionsModel.setBookId(id);
        transactionsModel.setSem(sem);
        transactionsModel.setDateTime(new Date().getTime());
        transactionsModel.setUserId(auth.getCurrentUser().getUid());
        transactionsModel.setPrice(Integer.parseInt(binding.totalAmount.getText().toString()));

        if (binding.ebook.isChecked()){
            database.getReference().child("books")
                    .child(Helper.getSem(CheckOut.this))
                    .child(id).child("buyers")
                    .child(auth.getCurrentUser().getUid()).setValue(true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            database.getReference().child("transactions2")
                                    .push()
                                    .setValue(transactionsModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            startActivity(new Intent(CheckOut.this, MyBooks.class));
                                            progressDialog.dismiss();

                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }else if (binding.paperback.isChecked()){
            dateTime = simpleDateFormat.format(new Date());
            DatabaseReference reference = database.getReference().child("paperBack")
                    .child(Helper.getSem(CheckOut.this))
                    .child(auth.getCurrentUser().getUid())
                    .child(Helper.getDateKey());
            reference.child("status").setValue("Success");

            if (binding.yes.isChecked()){
                reference.child("bookId").setValue(id)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("transactions2")
                                        .push()
                                        .setValue(transactionsModel)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                startActivity(new Intent(CheckOut.this, TrackActivity.class));
                                                progressDialog.dismiss();

                                            }
                                        });
                            }
                        });
            }else {
                reference.child("bookId").setValue("allBooks")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("transactions2")
                                        .push()
                                        .setValue(transactionsModel)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                startActivity(new Intent(CheckOut.this, TrackActivity.class));
                                                progressDialog.dismiss();

                                            }
                                        });
                            }
                        });
            }
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        //Toast.makeText(activity, "Payment Failed", Toast.LENGTH_LONG).show();
        Toast.makeText(activity, "Payment Failed", Toast.LENGTH_LONG).show();

//        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("Code Copied", s +"\ncode  = " + i + "\nPaymentData = " + paymentData);
//        clipboard.setPrimaryClip(clip);
    }
}