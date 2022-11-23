package com.example.soldout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.soldout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    String TAG = "ProductDetailsActivity";
    String sellerId;
    FirebaseFirestore db, dbUsers;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private ImageSlider imageSlider;

    ImageView productImage;
    TextView productName;
    TextView productDesc;
    TextView productPrice;
    TextView sellerInfo;
    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //Recieve prodcutID from intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("EXTRA_PRODUCT_ID");
        Log.d(TAG,productId);

        db = FirebaseFirestore.getInstance();
        dbUsers = FirebaseFirestore.getInstance();
//        StorageReference storageRef=storage.getReference();
        imageSlider = findViewById(R.id.imageSlider);
        productDesc = findViewById(R.id.productDesc);
        productName = findViewById(R.id.productName);
//        productImage = findViewById(R.id.productImage);
        productPrice = findViewById(R.id.productPrice);
        sellerInfo = findViewById(R.id.sellerInfo);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        db.collection("auctionProducts").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
//                        System.out.println(images.get(0));
                        for (int i = 0; i < images.size(); i++) {
                            slideModels.add(new SlideModel(images.get(i).toString(), ScaleTypes.CENTER_INSIDE));
                        }
                        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);

                        productName.setText(productTitle);
                        productDesc.setText(productDescTxt);


                        String bidTag = (String) product.get("highestBid");
                        String productPriceTxt;
                        if (bidTag != null) {
                            productPriceTxt = "Rs. " + bidTag;
                        } else {
                            productPriceTxt = "Free";
                        }
                        sellerId = product.get("sellerId").toString();
//                        System.out.println(sellerId);
                        productPrice.setText(productPriceTxt);
//                        final Uri productImageUri = Uri.parse(product.get("images").toString());
//                        storageRef.getFile(productImageUri)
//                                //returns a file-> make a temp file -> and then inflate the image view
//                        productImage.setImageURI();
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