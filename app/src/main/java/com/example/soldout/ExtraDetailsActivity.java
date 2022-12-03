package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtraDetailsActivity extends AppCompatActivity {
    final String TAG = "ExtraDetailsActivity";

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;

    EditText phone, room_no;
    Button confirmBtn;

    String phoneTxt, room_noTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        phone = findViewById(R.id.phone);
        room_no = findViewById(R.id.room_no);
        confirmBtn = findViewById(R.id.confirmBtn);


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneTxt = phone.getText().toString();
                room_noTxt = room_no.getText().toString();

                if (phoneTxt.trim().equals("") || room_noTxt.trim().equals("")) {
                    Toast.makeText(ExtraDetailsActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phoneTxt.length() != 10) {
                    Toast.makeText(ExtraDetailsActivity.this, "Phone number should be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> auctionProducts = new ArrayList<>();
                List<String> sellingProducts = new ArrayList<>();
                List<Map<String, Object>> notifications = new ArrayList<>();
                final String userIDTxt = currentUser.getUid().toString();
                String full_nameTxt = currentUser.getDisplayName();

                Map<String, Object> user = new HashMap<>();
                user.put("userID", userIDTxt);
                user.put("fullname", full_nameTxt);
                user.put("phone", phoneTxt);
                user.put("room no", room_noTxt);
                user.put("auctionProducts", auctionProducts);
                user.put("sellingProducts", sellingProducts);
                user.put("notifications", notifications);

                //adding user to users
                db.collection("users").document(userIDTxt)
                        .set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "add to db complete");
                                    startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
                                } else {
                                    Log.d(TAG, "add to db fails");
                                }
                            }
                        });
            }
        });
    }
}