package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class SellingProductsActivity extends AppCompatActivity {
    final String TAG = "SellingProductsActivity";

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SellingProductsRecyclerViewAdapter recyclerViewAdapter;
    FirebaseFirestore db;
    ArrayList<SellingProduct> sellingProductArrayList;

    Spinner spinnerTags;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_products);

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
        sellingProductArrayList = new ArrayList<SellingProduct>();

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Logging in ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new SellingProductsRecyclerViewAdapter(this, sellingProductArrayList);

        recyclerView.setAdapter(recyclerViewAdapter);

        spinnerTags = findViewById(R.id.spinnerTags);
        spinnerTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Inside onItemSelected");
                String queryTag = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "QUERY Tag: " + queryTag);
                queryTag=queryTag.trim().toLowerCase(Locale.ROOT);
                Log.d(TAG, "QUERY Tag after trim: " + queryTag);
                sellingProductArrayList.clear();
                db.collection("sellingProducts").whereArrayContains("tags", queryTag).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "task is successful: " + task.getResult().toString());
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        SellingProduct sellingProduct = document.toObject(SellingProduct.class);
                                        sellingProduct.setProductId(document.getId());
                                        sellingProductArrayList.add(sellingProduct);
                                    }
                                    recyclerViewAdapter.notifyDataSetChanged();
                                    if (progressBar.isShowing())
                                        progressBar.dismiss();
                                } else {
                                    Log.d(TAG, task.getException().getMessage());
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                EventChangListener();
                return;
            }
        });
    }
    //may become useless but still there in case of outliers
    private void EventChangListener() {

        db.collection("sellingProducts").orderBy("visitCount", Query.Direction.DESCENDING).
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
                                SellingProduct sellingProduct =dc.getDocument().toObject(SellingProduct.class);
                                sellingProduct.setProductId(dc.getDocument().getId());
                                sellingProductArrayList.add(sellingProduct);
                            }
                            recyclerViewAdapter.notifyDataSetChanged();
                            if (progressBar.isShowing())
                                progressBar.dismiss();
                        }
                    }
                });

    }
}