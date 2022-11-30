package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class LandingPageActivity extends AppCompatActivity {
    final String TAG = "LandingPageActivity";

    Button itemsForSaleBtn, itemsForAuctionBtn;

    RecyclerView concatRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    SellingProductsRecyclerViewAdapter sellingProductsRecyclerViewAdapter;
    AuctionProductsRecyclerViewAdapter auctionProductsRecyclerViewAdapter;
    ArrayList<SellingProduct> sellingProductArrayList;
    ArrayList<AuctionProduct> auctionProductArrayList;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressDialog progressBar;
    SearchView searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG,"Search entered");
                String q= searchBar.getQuery().toString();
                Log.d(TAG,"Query entered: "+ q);
                Intent intent = new Intent(getApplicationContext(),SearchResultActivity.class);
                intent.putExtra("query",q);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        itemsForAuctionBtn = findViewById(R.id.itemsForAuctionBtn);
        itemsForSaleBtn = findViewById(R.id.itemsForSaleBtn);

        itemsForAuctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AuctionProductsActivity.class));
            }
        });
        itemsForSaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SellingProductsActivity.class));
            }
        });


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        auctionProductArrayList = new ArrayList<AuctionProduct>();
        sellingProductArrayList = new ArrayList<SellingProduct>();

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Loading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        layoutManager = new GridLayoutManager(this, 2);
        concatRecyclerView = findViewById(R.id.concatRecyclerView);
        concatRecyclerView.setLayoutManager(layoutManager);

        sellingProductsRecyclerViewAdapter = new SellingProductsRecyclerViewAdapter(this, sellingProductArrayList);
        auctionProductsRecyclerViewAdapter = new AuctionProductsRecyclerViewAdapter(this, auctionProductArrayList);


        ConcatAdapter concatAdapter = new ConcatAdapter(sellingProductsRecyclerViewAdapter, auctionProductsRecyclerViewAdapter);
        concatRecyclerView.setAdapter(concatAdapter);


        SellingItemIntoViewListener();
        AuctionItemIntoViewListener();
    }

    private void SellingItemIntoViewListener() {
        db.collection("sellingProducts").orderBy("visitCount", Query.Direction.DESCENDING).limit(4).
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
                                SellingProduct sellingProduct = dc.getDocument().toObject(SellingProduct.class);
                                sellingProduct.setProductId(dc.getDocument().getId());
                                sellingProductArrayList.add(sellingProduct);
                            }
                            sellingProductsRecyclerViewAdapter.notifyDataSetChanged();
                            if (progressBar.isShowing())
                                progressBar.dismiss();
                        }
                    }
                });
    }

    private void AuctionItemIntoViewListener() {
        db.collection("auctionProducts").orderBy("visitCount", Query.Direction.DESCENDING).limit(4).
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
                                AuctionProduct auctionProduct = dc.getDocument().toObject(AuctionProduct.class);
                                auctionProduct.setProductId(dc.getDocument().getId());
                                auctionProductArrayList.add(auctionProduct);
                            }
                            auctionProductsRecyclerViewAdapter.notifyDataSetChanged();
                            if (progressBar.isShowing())
                                progressBar.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if the user is logged in
        //if logged in redirect to homepage
        //if not redirect to login activity where the user can decide if he wants to login or register
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

}