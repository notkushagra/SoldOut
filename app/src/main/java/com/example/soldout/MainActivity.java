package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    String productId = "M0t2hceFd4HUoZXBkoM7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        final Button signOutBtn = findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new handleSignOut());

        final Button sellItemsBtn = findViewById(R.id.sellItemsBtn);
        sellItemsBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SellingProductsActivity.class)));
        final Button auctionItemsBtn = findViewById(R.id.auctionItemsBtn);
        auctionItemsBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AuctionProductsActivity.class)));


        final Button productDetailBtn = findViewById(R.id.productDetailBtn);
        productDetailBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AuctionProductDetailsActivity.class);
            intent.putExtra("EXTRA_PRODUCT_ID", productId);
            //creates an intent and puts a pair inside the intent {key:value} -> { "EXTRA_PRODUCT_ID" : productId}
            Log.d("check ", intent.getStringExtra("EXTRA_PRODUCT_ID"));
            startActivity(intent);
        });

        final Button profilePageBtn = findViewById(R.id.profilePageBtn);
        profilePageBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfilePage.class);
            //intent.putExtra("EXTRA_USER_ID", user);
            //creates an intent and puts a pair inside the intent {key:value} -> { "EXTRA_PRODUCT_ID" : productId}
            //Log.d("check ", intent.getStringExtra("EXTRA_PRODUCT_ID"));
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}