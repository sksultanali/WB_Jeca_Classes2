package com.developerali.lshistutorial.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.developerali.lshistutorial.Helper;
import com.developerali.lshistutorial.databinding.ActivityAddMoneyBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMoney extends AppCompatActivity{

    ActivityAddMoneyBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;

//    private static final String TAG = AddMoney.class.getSimpleName();
//    private static final HashMap<Instamojo.Environment, String> env_options = new HashMap<>();
//
//    static {
//        env_options.put(Instamojo.Environment.TEST, "https://test.instamojo.com/");
//        env_options.put(Instamojo.Environment.PRODUCTION, "https://api.instamojo.com/");
//    }

//    private AlertDialog dialog;
//    private AppCompatEditText nameBox, emailBox, phoneBox, amountBox, descriptionBox;
//    private Instamojo.Environment mCurrentEnv = Instamojo.Environment.TEST;
//    private boolean mCustomUIFlow = false;
//
//    private MyBackendService myBackendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMoneyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        getProfileData();

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.upiSec.isChecked()){
                    binding.upiSection.setVisibility(View.VISIBLE);
                    binding.QrSection.setVisibility(View.GONE);
                } else if (binding.qrSec.isChecked()) {
                    binding.upiSection.setVisibility(View.GONE);
                    binding.QrSection.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.contactUs.setOnClickListener(v->{
            String message = "Hi, I am "+ Helper.My_Name +", Email- "+ Helper.My_Email + ". I have some problems in add money section! \n\n"+
                    "Profile Id- "+  Helper.My_PROFILE_ID;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + "8967254087" + "&text=" + message));
            startActivity(i);
        });













//        mCurrentEnv = Instamojo.Environment.TEST;
//        Instamojo.getInstance().initialize(AddMoney.this, mCurrentEnv);
//        mCustomUIFlow = false;






//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false); // if you want user to wait for some process to finish,
//        builder.setView(R.layout.layout_loading_dialog);
//        dialog = builder.create();
//
//        // Initialize the backend service client
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://sample-sdk-server.instamojo.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        myBackendService = retrofit.create(MyBackendService.class);



        binding.payNow.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("upi://pay?pa=8967254087@paytm"));
//            intent.putExtra("tn", "Payment for description");
            Intent chooser = Intent.createChooser(intent, "Add Money From...");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
            startActivity(chooser);
            Toast.makeText(AddMoney.this, "Loading...", Toast.LENGTH_SHORT).show();
            //createOrderOnServer();
        });

        






        binding.upiImage.setOnClickListener(v->{
            binding.payNow.performClick();
        });

        binding.profId.setOnClickListener(v->{
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Code Copied", binding.profId.getText().toString());
            Toast.makeText(AddMoney.this, "Id Copied Clipboard", Toast.LENGTH_LONG).show();
            clipboard.setPrimaryClip(clip);
        });

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });

    }


//    private void createOrderOnServer() {
//        GetOrderIDRequest request = new GetOrderIDRequest();
//        request.setEnv(mCurrentEnv.name());
//        request.setBuyerName("Shoyeb Khan");
//        request.setBuyerEmail("emptymind@gmail.com");
//        request.setBuyerPhone("9732824127");
//        request.setDescription("book buying");
//        request.setAmount("1");
//
//        Call<GetOrderIDResponse> getOrderIDCall = myBackendService.createOrder(request);
//        getOrderIDCall.enqueue(new retrofit2.Callback<GetOrderIDResponse>() {
//            @Override
//            public void onResponse(Call<GetOrderIDResponse> call, Response<GetOrderIDResponse> response) {
//                if (response.isSuccessful()) {
//                    String orderId = response.body().getOrderID();
//
//                    if (!mCustomUIFlow) {
//                        // Initiate the default SDK-provided payment activity
//                        initiateSDKPayment(orderId);
//
//                    } else {
//                        // OR initiate a custom UI activity
//                        initiateCustomPayment(orderId);
//                    }
//
//                } else {
//                    // Handle api errors
//                    try {
//                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        Log.d(TAG, "Error in response" + jObjError.toString());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetOrderIDResponse> call, Throwable t) {
//                // Handle call failure
//                Log.d(TAG, "Failure");
//            }
//        });
//    }
//
//    private void initiateSDKPayment(String orderID) {
//        Instamojo.getInstance().initiatePayment(this, orderID, this);
//    }
//
//    private void initiateCustomPayment(String orderID) {
//        Intent intent = new Intent(getBaseContext(), AddMoney.class);
//        intent.putExtra(Constants.ORDER_ID, orderID);
//        startActivityForResult(intent, Constants.REQUEST_CODE);
//    }
//
//    private void checkPaymentStatus(final String transactionID, final String orderID) {
//        if (transactionID == null && orderID == null) {
//            return;
//        }
//
//        if (dialog != null && !dialog.isShowing()) {
//            dialog.show();
//        }
//
//        showToast("Checking transaction status");
//        Call<GatewayOrderStatus> getOrderStatusCall = myBackendService.orderStatus(mCurrentEnv.name().toLowerCase(),
//                orderID, transactionID);
//        getOrderStatusCall.enqueue(new retrofit2.Callback<GatewayOrderStatus>() {
//            @Override
//            public void onResponse(Call<GatewayOrderStatus> call, final Response<GatewayOrderStatus> response) {
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//
//                if (response.isSuccessful()) {
//                    GatewayOrderStatus orderStatus = response.body();
//                    if (orderStatus.getStatus().equalsIgnoreCase("successful")) {
//                        showToast("Transaction still pending");
//                        return;
//                    }
//
//                    showToast("Transaction successful for id - " + orderStatus.getPaymentID());
//                    refundTheAmount(transactionID, orderStatus.getAmount());
//
//                } else {
//                    showToast("Error occurred while fetching transaction status");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GatewayOrderStatus> call, Throwable t) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (dialog != null && dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                        showToast("Failed to fetch the transaction status");
//                    }
//                });
//            }
//        });
//    }
//
//    private void refundTheAmount(String transactionID, String amount) {
//        if (transactionID == null || amount == null) {
//            return;
//        }
//
//        if (dialog != null && !dialog.isShowing()) {
//            dialog.show();
//        }
//
//        showToast("Initiating a refund for - " + amount);
//        Call<ResponseBody> refundCall = myBackendService.refundAmount(
//                mCurrentEnv.name().toLowerCase(),
//                transactionID, amount);
//        refundCall.enqueue(new retrofit2.Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//
//                if (response.isSuccessful()) {
//                    showToast("Refund initiated successfully");
//
//                } else {
//                    showToast("Failed to initiate a refund");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//
//                showToast("Failed to Initiate a refund");
//            }
//        });
//    }
//
//    private void showToast(final String message) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constants.REQUEST_CODE && data != null) {
//            String orderID = data.getStringExtra(Constants.ORDER_ID);
//            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
//            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);
//
//            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
//            if (transactionID != null || paymentID != null) {
//                checkPaymentStatus(transactionID, orderID);
//            } else {
//                showToast("Oops!! Payment was cancelled");
//            }
//        }
//    }
//
//    @Override
//    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
//        Log.d(TAG, "Payment complete");
//        showToast("Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
//                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);
//    }
//
//    @Override
//    public void onPaymentCancelled() {
//        Log.d(TAG, "Payment cancelled");
//        showToast("Payment cancelled by user");
//    }
//
//    @Override
//    public void onInitiatePaymentFailure(String errorMessage) {
//        Log.d(TAG, "Initiate payment failed");
//        showToast("Initiating payment failed. Error: " + errorMessage);
//    }

    private void getProfileData() {
        fireStore.collection("users")
                .document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Helper.My_Name = documentSnapshot.getString("name");
                            Helper.My_Email = documentSnapshot.getString("email");
                            Helper.My_PROFILE_ID = documentSnapshot.getId();

                            if (documentSnapshot.getLong("balance")!= null) {
                                binding.currentBalance.setText("Current Balance- ₹"
                                        +documentSnapshot.getLong("balance"));
                            }else {
                                binding.currentBalance.setText("Current Balance- ₹0");
                            }

                            binding.profId.setText(Helper.My_PROFILE_ID);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}