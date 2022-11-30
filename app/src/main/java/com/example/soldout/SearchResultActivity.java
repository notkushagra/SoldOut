package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity {
    final String TAG = "SearchResultActivity";


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
    String query;
    TextView searchResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

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

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "Search entered");
                String q = searchBar.getQuery().toString();
                Log.d(TAG, "Query entered: " + q);
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("query", q);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        concatRecyclerView = findViewById(R.id.concatRecyclerView);
        searchResultTextView=findViewById(R.id.searchResultTextView);

        query = getIntent().getStringExtra("query");
        searchResultTextView.setText("Search Results for \""+ query+"\"");
        query=query.replaceAll(" ","");
        query=query.trim().toLowerCase(Locale.ROOT);
        Log.d(TAG, "Query in search result: " + query);

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
        db.collection("sellingProducts")
                .whereArrayContains("keywords", query)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, task.getResult().toString());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SellingProduct sellingProduct = document.toObject(SellingProduct.class);
                                sellingProduct.setProductId(document.getId());
                                sellingProductArrayList.add(sellingProduct);
                                Log.d(TAG, "doc Added");
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            sellingProductsRecyclerViewAdapter.notifyDataSetChanged();
                            if (progressBar.isShowing())
                                progressBar.dismiss();
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                            Toast.makeText(SearchResultActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void AuctionItemIntoViewListener() {
        db.collection("auctionProducts")
                .whereArrayContains("keywords", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AuctionProduct auctionProduct = document.toObject(AuctionProduct.class);
                                auctionProduct.setProductId(document.getId());
                                auctionProductArrayList.add(auctionProduct);
                            }
                            auctionProductsRecyclerViewAdapter.notifyDataSetChanged();
                            if (progressBar.isShowing())
                                progressBar.dismiss();
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                            Toast.makeText(SearchResultActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}