package com.example.soldout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String productId = "M0t2hceFd4HUoZXBkoM7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        final Button signOutBtn = findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new handleSignOut());


         final Button sellItemsBtn = findViewById(R.id.sellItemsBtn);
        sellItemsBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SellingProductsActivity.class)) );
         final Button auctionItemsBtn = findViewById(R.id.auctionItemsBtn);
        auctionItemsBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AuctionProductsActivity.class)) );

        final com.google.android.material.bottomnavigation.BottomNavigationItemView  addProductMenuBtn = findViewById(R.id.addProductMenuBtn);
        addProductMenuBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddProduct.class));
//            addProductMenuBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
        });

        final com.google.android.material.bottomnavigation.BottomNavigationItemView  homeMenuBtn = findViewById(R.id.homeMenuBtn);
        findViewById(R.id.homeMenuBtn).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MainActivity.class)));

        final com.google.android.material.bottomnavigation.BottomNavigationItemView  profileMenuBtn = findViewById(R.id.profileMenuBtn);
//        profileMenuBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddProduct.class)));

        final Button productDetailBtn = findViewById(R.id.productDetailBtn);
        productDetailBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AuctionProductDetailsActivity.class);
            intent.putExtra("EXTRA_PRODUCT_ID", productId);
            //creates an intent and puts a pair inside the intent {key:value} -> { "EXTRA_PRODUCT_ID" : productId}
            Log.d("check ", intent.getStringExtra("EXTRA_PRODUCT_ID"));
            startActivity(intent);
        });

    }

    class handleSignOut implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FirebaseAuth.getInstance().signOut();
            onStart();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //check if the user is logged in
        //if logged in redirect to homepage
        //if not redirect to login activity where the user can decide if he wants to login or register
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }


}