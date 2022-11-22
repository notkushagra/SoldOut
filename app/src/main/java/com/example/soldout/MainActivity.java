package com.example.soldout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        final Button signOutBtn = findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new handleSignOut());

        final Button addProductBtn = findViewById(R.id.addProductBtn);
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddProduct.class));
            }
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