package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    //create object of database class
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //getting auth and instanace of firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //getting values from layout
        final EditText full_name = findViewById(R.id.full_name);
        final EditText phone = findViewById(R.id.phone);
        final EditText room_no = findViewById(R.id.room_no);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText confirm_password = findViewById(R.id.confirm_password);

        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNowBtn);
        final String emailPattern = "^(.+)@(\\S+)$";

        Log.d(TAG, "check");
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get data from editTexts
                final String full_nameTxt = full_name.getText().toString();
                final String phoneTxt = phone.getText().toString();
                final String room_noTxt = room_no.getText().toString();
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String confirm_passwordTxt = confirm_password.getText().toString();

                List<String> auctionProducts = new ArrayList<>();
                List<String> boughtProducts = new ArrayList<>();
                List<String> sellingProducts = new ArrayList<>();


                //check fill data
                if (full_nameTxt.trim().equals("") || phoneTxt.trim().equals("") || room_noTxt.trim().equals("") || emailTxt.trim().equals("") || passwordTxt.trim().equals("") || confirm_passwordTxt.trim().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                } else if (phoneTxt.length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Phone number should be 10 digits", Toast.LENGTH_SHORT).show();
                } else if (!Pattern.compile(emailPattern).matcher(emailTxt).matches()) {
                    Toast.makeText(RegisterActivity.this, "Not a valid Email", Toast.LENGTH_SHORT).show();
                } else if (passwordTxt.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password must be 8 characters long", Toast.LENGTH_SHORT).show();
                } else if (!passwordTxt.equals(confirm_passwordTxt)) {
                    //check if passwords match
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "create complete");
                                mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //send data to db
                                            Log.d(TAG, "signin complete");

                                            currentUser = mAuth.getCurrentUser();
                                            Log.d(TAG, currentUser.toString());

                                            Map<String, Object> user = new HashMap<>();
                                            final String userIDTxt = currentUser.getUid().toString();
                                            user.put("userID", userIDTxt);
                                            user.put("fullname", full_nameTxt);
                                            user.put("phone", phoneTxt);
                                            user.put("room no", room_noTxt);
                                            user.put("auctionProducts", auctionProducts);
//                                            user.put("boughtProducts", boughtProducts);
                                            user.put("sellingProducts", sellingProducts);

                                            //adding user to users
                                            db.collection("users").document(userIDTxt)
                                                    .set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "add to db complete");
                                                            } else {
                                                                Log.d(TAG, "add to db fails");
                                                            }
                                                        }
                                                    });

                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                        } else {
                                            Log.d(TAG, "sign in fail");
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "create fail");
                                return;
                            }
                        }
                    });
                }
            }
        });

        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

        });

    }
}