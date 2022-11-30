package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userId;
    EditText editUserName;
    EditText phoneNumber;
    EditText roomNo;

    Button addProductBtn, signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        LinearLayout myProducts = findViewById(R.id.myProducts);
        LayoutInflater inflater = LayoutInflater.from(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

        phoneNumber = findViewById(R.id.phoneNo);
        roomNo = findViewById(R.id.roomNo);
        editUserName = findViewById(R.id.editUserName);
        addProductBtn = findViewById(R.id.addProductBtn);
        signOutBtn = findViewById(R.id.signOutBtn);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));;
                onStart();
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddProduct.class));
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profileMenuBtn);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeMenuBtn:
                        startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.addProductMenuBtn:
                        startActivity(new Intent(getApplicationContext(), AddProduct.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profileMenuBtn:
                        startActivity(new Intent(getApplicationContext(), ProfilePage.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> user = new HashMap<>();
                        user = doc.getData();
                        final String fullname = (String) user.get("fullname");
                        final String phoneNo = (String) user.get("phone");
                        final String room = (String) user.get("room no");
                        List<String> sellingProducts = (List<String>) user.get("sellingProducts");
                        List<String> sellingProductsURIs = new ArrayList<>();
                        phoneNumber.setText(phoneNo, TextView.BufferType.EDITABLE);
                        roomNo.setText(room, TextView.BufferType.EDITABLE);
                        editUserName.setText(fullname, TextView.BufferType.EDITABLE);
                        for (int i = 0; i < sellingProducts.size(); i++) {
                            db.collection("sellingProducts").document(sellingProducts.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()) {
                                            Map<String, Object> product = new HashMap<>();
                                            product = doc.getData();
                                            List<String> image = (List<String>) product.get("images");
                                            if (!image.isEmpty())
                                                sellingProductsURIs.add(image.get(0));
                                            else
                                                Toast.makeText(ProfilePage.this, "Some products might not have any pictures", Toast.LENGTH_SHORT).show();
                                        }
                                        for (int i = 0; i < sellingProductsURIs.size(); i++) {
                                            View view = inflater.inflate(R.layout.item, myProducts, false);
                                            TextView textView = view.findViewById(R.id.sliderImageText1);
                                            textView.setText("item ");
                                            ImageView imageView = view.findViewById(R.id.sliderImage);
                                            Picasso.get().load(sellingProductsURIs.get(i)).resize(700, 700).placeholder(R.drawable.loading_gif).centerCrop().into(imageView);

                                            myProducts.addView(view);
                                        }
                                    }
                                }

                            });
                        }


                    } else {
//                         Log.d(TAG, "Doc does not exist");
                    }
                } else {
//                     Log.d(TAG, task.getException().getMessage());
                }

            }
        });
    }
}
