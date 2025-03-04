package com.developerali.wbjecaclasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.developerali.wbjecaclasses.Models.UserModel;
import com.developerali.wbjecaclasses.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    FirebaseDatabase data;
    GoogleSignInClient signInClient;
    String uid, name, email, password;
    Uri selectedImage;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        data = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("creating new account...");
        dialog.setCancelable(false);

        //referCode = getReferCode();
        registerForToken();

        binding.goBtn.setOnClickListener(v->{
            onBackPressed();
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        //step 3
        binding.imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT); //sob content samne esbe!
                i.setType("image/*"); //datatype bola holo
                startActivityForResult(i, 45);

            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = binding.name.getText().toString();
                email = binding.email.getText().toString();
                password = binding.password.getText().toString();

                if (name.isEmpty()) {
                    binding.name.setError("Enter Name");
                } else if (email.isEmpty()) {
                    binding.email.setError("Enter Email");
                } else if (password.isEmpty()) {
                    binding.password.setError("Enter Password");
                }else {

                    dialog.show();
                    if (selectedImage != null){

                        String key = data.getReference().push().getKey();

                        StorageReference reference = storage.getReference().child("profiles").child(key);     // storage e folder banalam
                        reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(SignUp.this, "setting profile picture...", Toast.LENGTH_SHORT).show();

                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {    //ekhane uri hochche user er profile link!

                                            // ekhane theke sob data load hochche database e -->

                                            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()) {

                                                        Toast.makeText(SignUp.this, "please wait few seconds...", Toast.LENGTH_SHORT).show();
                                                        uid = task.getResult().getUser().getUid();

                                                        String imageUrl = uri.toString();   // image direct store hoina tai url banate holo!

                                                        UserModel user = new UserModel();   //constructor sequence rakhte hobe variable k!
                                                        user.setName(name);
                                                        user.setEmail(email);

                                                        Helper.My_Name = name;

                                                        if (token != null){
                                                            user.setToken(token);
                                                        }
                                                        user.setImageUrl(imageUrl);

                                                        database
                                                                .collection("users")
                                                                .document(uid)
                                                                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()) {
                                                                            dialog.dismiss();
                                                                            startActivity(new Intent(SignUp.this, MainActivity.class));
                                                                            finish();
                                                                        } else {
                                                                            dialog.dismiss();
                                                                            Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        dialog.dismiss();
                                                        Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });


                        // image jodi select na kore ? tokhon eta run hobe !

                    }else {

                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    uid = task.getResult().getUser().getUid();
                                    String imageUrl = "null";

                                    UserModel user = new UserModel();   //constructor sequence rakhte hobe variable k!
                                    user.setName(name);
                                    user.setEmail(email);
                                    Helper.My_Name = name;

                                    if (token != null){
                                        user.setToken(token);
                                    }
                                    user.setImageUrl(imageUrl);

                                    database
                                            .collection("users")
                                            .document(uid)
                                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        startActivity(new Intent(SignUp.this, MainActivity.class));
                                                        finish();
                                                    } else {
                                                        dialog.dismiss();
                                                        Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }



                }
            }
        });



        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(SignUp.this, googleSignInOptions);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            signInClient.signOut().addOnCompleteListener(this, task -> {

            });
        }

        binding.googleLog.setOnClickListener(v->{
            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, 85);
    }


    // step 4   //user profile
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (data.getData() != null){
                selectedImage = data.getData();
                binding.imageprofile.setImageURI(data.getData());
            }
        }

        if (requestCode == 85 ){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(), account.getEmail(),
                        account.getDisplayName(), account.getPhotoUrl().toString());
                dialog.show();

            } catch (ApiException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    void registerForToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        token = task.getResult();
                    }
                });
    }
    private void firebaseAuthWithGoogle(String idToken, String email, String name, String photoUrl) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            UserModel userModel = new UserModel();
                            userModel.setName(name);
                            userModel.setEmail(email);

                            Helper.My_Name = name;

                            if (token != null){
                                userModel.setToken(token);
                            }
                            if (photoUrl != null){
                                userModel.setImageUrl(photoUrl);
                            }

                            database.collection("users")
                                    .document(auth.getCurrentUser().getUid())
                                    .set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                dialog.dismiss();
                                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                                finish();
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
