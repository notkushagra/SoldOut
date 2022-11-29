package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class AuctionProductsActivity extends AppCompatActivity {

    final String TAG = "AuctionProductsActivity";

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AuctionProductsRecyclerViewAdapter recyclerViewAdapter;
    FirebaseFirestore db;
    ArrayList<AuctionProduct> auctionProductArrayList;

    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_products);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
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

        db = FirebaseFirestore.getInstance();
        auctionProductArrayList = new ArrayList<AuctionProduct>();

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Logging in ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new AuctionProductsRecyclerViewAdapter(this, auctionProductArrayList);

        recyclerView.setAdapter(recyclerViewAdapter);

        EventChangListener();
    }

    private void EventChangListener() {

        db.collection("auctionProducts").orderBy("visitCount", Query.Direction.DESCENDING).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressBar.isShowing())
                                progressBar.dismiss();
                            Log.d(TAG, error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                AuctionProduct auctionProduct =dc.getDocument().toObject(AuctionProduct.class);
                                auctionProduct.setProductId(dc.getDocument().getId());
                                auctionProductArrayList.add(auctionProduct);
                            }
                            recyclerViewAdapter.notifyDataSetChanged();
                            if (progressBar.isShowing())
                                progressBar.dismiss();
                        }
                    }
                });

    }
}