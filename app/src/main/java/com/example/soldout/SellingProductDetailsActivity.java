package com.example.soldout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellingProductDetailsActivity extends AppCompatActivity {

    String TAG = "SellingProductDetailsActivity";
    String sellerId;
    FirebaseFirestore db, dbUsers;

    private ImageSlider imageSlider;

    TextView productName;
    TextView productDesc;
    TextView productPrice;
    TextView sellerInfo;
    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_product_details);

        //Recieve prodcutID from intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("EXTRA_PRODUCT_ID");
        Log.d(TAG,productId);

        db = FirebaseFirestore.getInstance();
        dbUsers = FirebaseFirestore.getInstance();
        imageSlider = findViewById(R.id.imageSlider);
        productDesc = findViewById(R.id.productDesc);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        sellerInfo = findViewById(R.id.sellerInfo);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        db.collection("sellingProducts").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> product = new HashMap<>();
                        product = doc.getData();
                        final String productDescTxt = product.get("desc").toString();
                        final String productTitle = product.get("name").toString();

                        List<String> images = (List<String>) product.get("images");

                        for (int i = 0; i < images.size(); i++) {
                            slideModels.add(new SlideModel(images.get(i).toString(), ScaleTypes.CENTER_INSIDE));
                        }
                        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);

                        productName.setText(productTitle);
                        productDesc.setText(productDescTxt);


                        String bidTag = (String) product.get("price");
                        String productPriceTxt;
                        if (bidTag != null) {
                            productPriceTxt = "Rs. " + bidTag;
                        } else {
                            productPriceTxt = "No Bids Yet";
                        }
                        sellerId = product.get("sellerId").toString();
                        productPrice.setText(productPriceTxt);

                        dbUsers.collection("users").document(sellerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        Map<String, Object> seller = new HashMap<>();
                                        seller = doc.getData();
                                        final String sellerData = "Seller Info \n" + seller.get("fullname") + "\n" + seller.get("room no").toString() + "\n" + seller.get("phone").toString();
                                        sellerInfo.setText(sellerData);
                                    } else {
                                        Log.d(TAG, "Doc does not exist");
                                    }
                                } else {
                                    Log.d(TAG, task.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Doc does not exist");
                    }
                } else {
                    Log.d(TAG, task.getException().getMessage());
                }
            }
        });
    }
}