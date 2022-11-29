package com.example.soldout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    TextView fullName;
    EditText currentUserName;
    EditText phoneNumber;
    EditText roomNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        LinearLayout myProducts = findViewById(R.id.myProducts);
        LayoutInflater inflater = LayoutInflater.from(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        System.out.println(currentUser.getUid() + " this is the current user");
        userId = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        fullName = findViewById(R.id.userName);
        phoneNumber = findViewById(R.id.phoneNo);
        roomNo = findViewById(R.id.roomNo);
        currentUserName = findViewById(R.id.editUserName);

        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> user = new HashMap<>();
                        user = doc.getData();
                        final String fullname = (String) user.get("fullname");
                        final String phoneNo = (String)user.get("phone");
                        final String room = (String)user.get("room no");
                        fullName.setText(fullname);
                        List<String> sellingProducts = (List<String>) user.get("sellingProducts");
                        List<String> sellingProductsURIs = new ArrayList<>();
                        phoneNumber.setText(phoneNo, TextView.BufferType.EDITABLE);
                        roomNo.setText(room,TextView.BufferType.EDITABLE);
                        currentUserName.setText(fullname,TextView.BufferType.EDITABLE);
                        for(int i = 0; i < sellingProducts.size(); i++) {
                            db.collection("sellingProducts").document(sellingProducts.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()) {
                                            Map<String, Object> product = new HashMap<>();
                                            product = doc.getData();
                                            List<String> image = (List<String>) product.get("images");
                                            sellingProductsURIs.add(image.get(0));
                                            System.out.println("linkii "+image.get(0));
                                            System.out.println("sizee" + sellingProductsURIs.size());

                                        }
                                        System.out.println("size= " + sellingProductsURIs.size());
                                        for (int i = 0; i < sellingProductsURIs.size(); i++) {
                                            System.out.println("i=" +i);
                                            System.out.println("klkl");
                                            View view = inflater.inflate(R.layout.item, myProducts, false);
                                            TextView textView = view.findViewById(R.id.sliderImageText1);
                                            textView.setText("item ");
                                            ImageView imageView = view.findViewById(R.id.sliderImage);
                                            Picasso.get().load(sellingProductsURIs.get(i)).resize(700, 700).placeholder(R.drawable.loading_gif).centerCrop().into(imageView);
                                            //image = BitmapFactory.decodeStream(img);
                                           // imageView.setImageResource(R.drawable.background_sold_out);
                                            myProducts.addView(view);
                                        }
                                    }
                                }

                            });
                        }


                        } else {
                        // Log.d(TAG, "Doc does not exist");
                    }
                } else {
                    // Log.d(TAG, task.getException().getMessage());
                }




//                LinearLayout myBids = findViewById(R.id.myBids);
//                LayoutInflater inflaterBids = LayoutInflater.from(this);
//
////            db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                @Override
////                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////
////                }
//                for (int i = 0; i < 6; i++) {
//                    View view = inflaterBids.inflate(R.layout.item2, myBids, false);
//
//                    TextView textView = view.findViewById(R.id.sliderImageText2);
//                    textView.setText("item " + i);
//
//                    ImageView imageView = view.findViewById(R.id.sliderImage2);
//
//                    imageView.setImageResource(R.drawable.ic_launcher_background);
//                    myBids.addView(view);
//
//
//                }

            }
        });
    }
}
