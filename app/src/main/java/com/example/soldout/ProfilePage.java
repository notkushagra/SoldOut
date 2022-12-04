package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {
    final String TAG = "ProfilePage";
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userId;
    TextView heyMsg;
    private RecyclerView boughtProductsRecyclerView;
    SellingProductsRecyclerViewAdapter boughtProductRecyclerViewAdapter;
    ArrayList<SellingProduct> boughtProductArrayList;

    private RecyclerView bidProductsRecyclerView;
    AuctionProductsRecyclerViewAdapter bidProductRecyclerViewAdapter;
    ArrayList<AuctionProduct> bidProductArrayList;

    private RecyclerView notificationRecyclerView;
    NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    ArrayList<Notification> notificationArrayList;

    Button addProductBtn, signOutBtn;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

        addProductBtn = findViewById(R.id.addProductBtn);
        signOutBtn = findViewById(R.id.signOutBtn);
        heyMsg = findViewById(R.id.heyMsg);

        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("fullname") != null) {
                    userName = documentSnapshot.get("fullname").toString();
                    String firstName = userName;

                    if (firstName.contains(" ")) {
                        firstName = firstName.substring(0, firstName.indexOf(" "));
                    }
                    heyMsg.setText("Hey " + firstName + "!");
                }
            }
        });

        notificationArrayList = new ArrayList<Notification>();
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(this, notificationArrayList);
        notificationRecyclerView.setAdapter(notificationRecyclerViewAdapter);

        //Setting your bids and buys
        boughtProductArrayList = new ArrayList<SellingProduct>();
        boughtProductsRecyclerView = findViewById(R.id.boughtProductsRecyclerView);
        boughtProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        boughtProductRecyclerViewAdapter = new SellingProductsRecyclerViewAdapter(this, boughtProductArrayList);
        boughtProductsRecyclerView.setAdapter(boughtProductRecyclerViewAdapter);

        bidProductArrayList = new ArrayList<AuctionProduct>();
        bidProductsRecyclerView = findViewById(R.id.bidProductsRecyclerView);
        bidProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        bidProductRecyclerViewAdapter = new AuctionProductsRecyclerViewAdapter(this, bidProductArrayList);
        bidProductsRecyclerView.setAdapter(bidProductRecyclerViewAdapter);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                AuthUI.getInstance().signOut(getApplicationContext());
                startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
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

        //gets the data of user bids and purchases
        EventChangListener();
        NotificationChangeListener();
    }

    private void NotificationChangeListener() {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "doc exists");
                        User user = document.toObject(User.class);
                        Log.d(TAG, user.toString());
                        List<Notification> notifications = user.notifications;
                        Log.d(TAG, notifications.toString());
                        if (notifications != null) {
                            for (int i = 0; i < notifications.size(); i++) {
                                Notification notification = notifications.get(i);
                                notificationArrayList.add(notification);
                            }
                        }
                        Log.d(TAG, "Notifications array size - " + notifications.size());
                        notificationRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "doc doesnt exists");
                    }
                } else {
                    Log.d(TAG, task.getException().getMessage());
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void EventChangListener() {
        db.collection("sellingProducts").whereEqualTo("buyerId", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "task is successful: " + task.getResult().toString());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                SellingProduct sellingProduct = document.toObject(SellingProduct.class);
                                sellingProduct.setProductId(document.getId());
                                boughtProductArrayList.add(sellingProduct);
                            }
                            if (boughtProductArrayList != null)
                                Log.d(TAG, String.valueOf(boughtProductArrayList.size()));

                            boughtProductRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("auctionProducts").whereArrayContains("bidders", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "task is successful: " + task.getResult().toString());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                AuctionProduct auctionProduct = document.toObject(AuctionProduct.class);
                                auctionProduct.setProductId(document.getId());
                                bidProductArrayList.add(auctionProduct);
                            }
                            if (bidProductArrayList != null)
                                Log.d(TAG, String.valueOf(bidProductArrayList.size()));

                            bidProductRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
