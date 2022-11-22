package com.example.soldout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddProduct extends AppCompatActivity {
    final String TAG = "AddProductActivity";

    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    EditText productName, productDesc, productPrice;
    ImageView imgUplaoded;
    Button addImgBtn, sellBtn, auctionBtn;

    Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
//        storageRef = storage.getReference();

        imgUplaoded = findViewById(R.id.imgUplaoded);
        addImgBtn = findViewById(R.id.addImgBtn);
        sellBtn = findViewById(R.id.sellBtn);
        auctionBtn = findViewById(R.id.auctionBtn);
        productDesc = findViewById(R.id.productDesc);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);

        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Wait a minute ;)");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                addImageResultLauncher.launch(iGallery);
            }
        });

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();

                //get data from entry points
                final String productNameTxt = productName.getText().toString();
                final String productDescTxt = productDesc.getText().toString();
                final String productPriceTxt = productPrice.getText().toString();
                //imageUri comes from add image button

                if (ImageUri == null) {
                    progressBar.dismiss();
                    Toast.makeText(AddProduct.this, "Attach at least one image", Toast.LENGTH_SHORT).show();
                } else if (productNameTxt.trim().equals("") || productDescTxt.trim().equals("") || productPriceTxt.trim().equals("")) {
                    progressBar.dismiss();
                    Toast.makeText(AddProduct.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                } else {
                    //save instance in firestore
                    Map<String, Object> product = new HashMap<>();
                    boolean intiSoldStatus = false;
                    int initVisitCount = 0;
                    List<String> tags = new ArrayList<>();
                    List<String> images = new ArrayList<>();
                    final String userIDTxt = currentUser.getUid().toString();

                    //giving the uploaded file a name
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                    Date now = new Date();
                    String fileName = formatter.format(now);
                    String filePath = "images/" + currentUser.getUid() + "/" + productNameTxt.toLowerCase() + "/" + fileName;
                    images.add(filePath);
                    storageRef = storage.getReference(filePath);

                    product.put("name", productNameTxt);
                    product.put("desc", productDescTxt);
                    product.put("price", productPriceTxt);
                    product.put("buyerId", "");
                    product.put("sellerId", userIDTxt);
                    product.put("soldStatus", intiSoldStatus);
                    product.put("visitCount", initVisitCount);
                    product.put("tags", tags);
                    product.put("images", images);

                    //adding user to Users
                    db.collection("sellingProducts")
                            .add(product)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, task.getResult().toString());
                                        Log.d(TAG, task.getResult().getId());
                                        Log.d(TAG, "check");
                                        final String productId = task.getResult().getId();

                                        //save image in cloud storage

                                        storageRef.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    imgUplaoded.setImageURI(null);
                                                    Toast.makeText(AddProduct.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    db.collection("users").document(userIDTxt).update("sellingProducts", FieldValue.arrayUnion(productId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressBar.dismiss();
                                                                startActivity(new Intent(AddProduct.this, MainActivity.class));
                                                            } else {
                                                                progressBar.dismiss();
                                                                Toast.makeText(AddProduct.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    progressBar.dismiss();
                                                    Toast.makeText(AddProduct.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        progressBar.dismiss();
                                        Log.d(TAG, "add to db fails");
                                    }
                                }
                            });
                }
            }
        });

        auctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();

                final String productNameTxt = productName.getText().toString();
                final String productDescTxt = productDesc.getText().toString();
                final String productPriceTxt = productPrice.getText().toString();
                //imageUri comes from add image button

                if (ImageUri == null) {
                    progressBar.dismiss();
                    Toast.makeText(AddProduct.this, "Attach at least one image", Toast.LENGTH_SHORT).show();
                } else if (productNameTxt.trim().equals("") || productDescTxt.trim().equals("") || productPriceTxt.trim().equals("")) {
                    progressBar.dismiss();
                    Toast.makeText(AddProduct.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                } else {
                    //save instance in firestore
                    Map<String, Object> product = new HashMap<>();
                    boolean intiSoldStatus = false;
                    int initVisitCount = 0;
                    List<String> tags = new ArrayList<>();
                    List<String> images = new ArrayList<>();
                    final String userIDTxt = currentUser.getUid().toString();

                    //giving the uploaded file a name
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                    Date now = new Date();
                    String fileName = formatter.format(now);
                    String filePath = "images/" + currentUser.getUid() + "/" + productNameTxt.toLowerCase() + "/" + fileName;
                    images.add(filePath);
                    storageRef = storage.getReference(filePath);

                    product.put("name", productNameTxt);
                    product.put("desc", productDescTxt);
                    product.put("highestBid", productPriceTxt);
                    product.put("highestBidderId", "");
                    product.put("sellerId", userIDTxt);
                    product.put("soldStatus", intiSoldStatus);
                    product.put("visitCount", initVisitCount);
                    product.put("tags", tags);
                    product.put("images", images);

                    //adding user to Users
                    db.collection("auctionProducts")
                            .add(product)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, task.getResult().toString());
                                        Log.d(TAG, task.getResult().getId());
                                        Log.d(TAG, "check");
                                        final String productId = task.getResult().getId();

                                        //save image in cloud storage

                                        storageRef.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    imgUplaoded.setImageURI(null);
                                                    Toast.makeText(AddProduct.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    db.collection("users").document(userIDTxt).update("auctionProducts", FieldValue.arrayUnion(productId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressBar.dismiss();
                                                                startActivity(new Intent(AddProduct.this, MainActivity.class));
                                                            } else {
                                                                progressBar.dismiss();
                                                                Toast.makeText(AddProduct.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    progressBar.dismiss();
                                                    Toast.makeText(AddProduct.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        progressBar.dismiss();
                                        Log.d(TAG, "add to db fails");
                                    }
                                }
                            });
                }
            }
        });

    }

    ActivityResultLauncher<Intent> addImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    final int resultCode = result.getResultCode();
                    if (resultCode == Activity.RESULT_OK) {
                        // There are no request codes so no need to check for req code
                        Intent data = result.getData();
                        ImageUri = data.getData();
                        imgUplaoded.setImageURI(ImageUri);

                    }
                }
            });
}