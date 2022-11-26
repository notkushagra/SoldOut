package com.example.soldout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellingProductsActivity extends AppCompatActivity {
    final String TAG = "SellingProductsActivity";

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SellingProductsRecyclerViewAdapter recyclerViewAdapter;
    FirebaseFirestore db;
    ArrayList<SellingProduct> sellingProductArrayList;

    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_products);

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

        EventChangListener();
    }

    private void EventChangListener() {

        db.collection("sellingProducts").
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