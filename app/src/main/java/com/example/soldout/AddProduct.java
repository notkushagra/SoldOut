package com.example.soldout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

public class AddProduct extends AppCompatActivity {
    final String TAG = "AddProductActivity";

    String currentUserId;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    EditText productName, productDesc, productPrice, productTime;
    Button addImgBtn, sellBtn, auctionBtn;
    ProgressDialog progressBar;
    Spinner spinnerTags;

    String productNameTxt, productDescTxt, productPriceTxt, productId;
    Long productHoursNum;
    Timestamp productTimeFinal;
    List<String> images;
    ArrayList<Uri> mImageUriArray; // contains Uri of all the images

    ArrayList<SlideModel> slideModels;
    private ImageSlider imageSlider;
    String imageEncoded;
    List<String> imagesEncodedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Log.d(TAG, "OnCreate");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.addProductMenuBtn);
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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        currentUserId = currentUser.getUid();

        addImgBtn = findViewById(R.id.addImgBtn);
        sellBtn = findViewById(R.id.sellBtn);
        auctionBtn = findViewById(R.id.auctionBtn);
        productDesc = findViewById(R.id.productDesc);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productTime = findViewById(R.id.addHoursTxt);
        spinnerTags = findViewById(R.id.spinnerTags);

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Wait a minute ;)");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        imageSlider = findViewById(R.id.imageSlider);
        slideModels = new ArrayList<>();
        images = new ArrayList<>();
        mImageUriArray = new ArrayList<Uri>();

        addImgBtn.setOnClickListener(new handleAddImage());
        sellBtn.setOnClickListener(new handleSellBtn());
        auctionBtn.setOnClickListener(new handleAuctionBtn());
    }

    class handleAddImage implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Clicked Add Image button");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            addImageResultLauncher.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> addImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            try {
                final int resultCode = result.getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    // There are no request codes so no need to check for req code
                    Intent data = result.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    imagesEncodedList = new ArrayList<String>();

                    if (data.getData() != null) {
                        // SINGLE FILE SELECTED
                        Uri mImageUri = data.getData();
                        mImageUriArray.add(mImageUri);
                        Log.d(TAG, "added only 1 imgUri in mImageUri");

                    } else {
                        if (data.getClipData() != null) {
                            // MULTIPLE FILE SELECTED
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {
                                String finalI = String.valueOf(i);
                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mImageUriArray.add(uri);
                                Log.d(TAG, "added image: " + finalI);

                            }
                            Log.v(TAG, "Selected Images - " + mImageUriArray.size());
                        }
                    }
                } else {
                    Log.d(TAG, "No image picked");
                    Toast.makeText(AddProduct.this, "Pick at least one image", Toast.LENGTH_LONG).show();
                }

                for (int i = 0; i < mImageUriArray.size(); i++) {
                    slideModels.add(new SlideModel(mImageUriArray.get(i).toString(), ScaleTypes.CENTER_INSIDE));
                }
                imageSlider.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);

            } catch (Exception e) {
                Log.d(TAG, "catch block " + e.getMessage());
                Toast.makeText(AddProduct.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    });

    class handleAuctionBtn implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            progressBar.show();

            //get data from entry points
            productNameTxt = productName.getText().toString();
            productDescTxt = productDesc.getText().toString();
            productPriceTxt = productPrice.getText().toString();


            if (mImageUriArray == null || mImageUriArray.size() == 0) {
                progressBar.dismiss();
                Toast.makeText(AddProduct.this, "Attach at least one image", Toast.LENGTH_SHORT).show();
                return;
            } else if (productNameTxt.trim().equals("") || productDescTxt.trim().equals("") || productPriceTxt.trim().equals("") || productTime.getText().toString().trim().equals("")) {
                progressBar.dismiss();
                Toast.makeText(AddProduct.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Product entry in auctionProducts");

            productHoursNum = Long.valueOf(productTime.getText().toString());
            Long num = Long.valueOf(1000 * 60 * 60);
            Long start = System.currentTimeMillis();
            productTimeFinal = new Timestamp(start + (num * productHoursNum));

            Map<String, Object> product = new HashMap<>();
            boolean initSoldStatus = false;
            int initVisitCount = 0;
            List<String> tags = new ArrayList<>();
            List<String> images = new ArrayList<>();
            tags.add("all");
            tags.add(spinnerTags.getSelectedItem().toString().toLowerCase(Locale.ROOT));
            //putting keywords in products
            List<String> keywords = new ArrayList<>();
            keywords = generateKeywords(productNameTxt);
            product.put("keywords", keywords);
            List<String> bidders = new ArrayList<>();

            product.put("name", productNameTxt);
            product.put("desc", productDescTxt);
            product.put("price", productPriceTxt);
            product.put("highestBidderId", "");
            product.put("sellerId", currentUserId);
            product.put("soldStatus", initSoldStatus);
            product.put("visitCount", initVisitCount);
            product.put("tags", tags);
            product.put("images", images);
            product.put("bidders", bidders);
            product.put("expiryTime", productTimeFinal);

            db.collection("auctionProducts")
                    .add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                //setting productID to be used later
                                productId = task.getResult().getId();
                                Log.d(TAG, "SellingProduct added in auction products: " + productId);
                                storeProductIdInUsers("auctionProducts");
                            } else {
                                progressBar.dismiss();
                                String ErrorMsg = task.getException().getMessage().toString();
                                Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, ErrorMsg);
                            }
                        }
                    });
        }
    }

    class handleSellBtn implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            progressBar.show();

            //get data from entry points
            productNameTxt = productName.getText().toString();
            productDescTxt = productDesc.getText().toString();
            productPriceTxt = productPrice.getText().toString();

            if (mImageUriArray == null || mImageUriArray.size() == 0) {
                progressBar.dismiss();
                Toast.makeText(AddProduct.this, "Attach at least one image", Toast.LENGTH_SHORT).show();
                return;
            } else if (productNameTxt.trim().equals("") || productDescTxt.trim().equals("") || productPriceTxt.trim().equals("")) {
                progressBar.dismiss();
                Toast.makeText(AddProduct.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                return;
            }

            //put product entry in
            Log.d(TAG, "SellingProduct entry in sellingProducts");
            Map<String, Object> product = new HashMap<>();
            boolean initSoldStatus = false;
            int initVisitCount = 0;
            List<String> tags = new ArrayList<>();
            List<String> images = new ArrayList<>();
            tags.add("all");
            tags.add(spinnerTags.getSelectedItem().toString().toLowerCase(Locale.ROOT));

            //putting keywords in products
            List<String> keywords = new ArrayList<>();
            keywords = generateKeywords(productNameTxt);
            product.put("keywords", keywords);

            product.put("name", productNameTxt);
            product.put("desc", productDescTxt);
            product.put("price", productPriceTxt);
            product.put("buyerId", "");
            product.put("sellerId", currentUserId);
            product.put("soldStatus", initSoldStatus);
            product.put("visitCount", initVisitCount);
            product.put("tags", tags);
            product.put("images", images);

            db.collection("sellingProducts")
                    .add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                //setting productID to be used later
                                productId = task.getResult().getId();
                                Log.d(TAG, "SellingProduct added in selling products: " + productId);
                                storeProductIdInUsers("sellingProducts");
                            } else {
                                progressBar.dismiss();
                                String ErrorMsg = task.getException().getMessage().toString();
                                Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, ErrorMsg);
                            }
                        }
                    });
        }
    }

    void storeProductIdInUsers(String typeOfProduct) {
        db.collection("users").document(currentUserId).update(typeOfProduct, FieldValue.arrayUnion(productId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Added SellingProduct: " + productId + " in " + typeOfProduct + " section of user " + currentUserId);
                    storeImagesInFireStore(typeOfProduct);
                } else {
                    progressBar.dismiss();
                    String ErrorMsg = task.getException().getMessage().toString();
                    Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, ErrorMsg);
                }
            }
        });
    }

    void storeImagesInFireStore(String typeOfProduct) {
        progressBar.setMessage("Image Upload Takes Some Time");
        int n = mImageUriArray.size();
        Log.d(TAG, "About to put images in firestore : " + n + " images");

        for (int i = 0; i < n; i++) {

            //generating filePath and fileName
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
            Date now = new Date();
            String fileName = formatter.format(now) + "_image_" + (i + 1);
            String filePath = "images/" + currentUser.getUid() + "/" + productNameTxt.toLowerCase() + "/" + fileName;

            // now we get reference to the area our file is uploaded in
            StorageReference storageRef = storage.getReference(filePath);
            Uri ImageUri = mImageUriArray.get(i);

            int finalI = i;
            storageRef.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Added " + finalI + " file in cloud firebase");

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "got DownloadUrl of " + finalI + " from cloud firebase");
                                String imgUrl = uri.toString();
                                db.collection(typeOfProduct).document(productId).update("images", FieldValue.arrayUnion(imgUrl)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "ImgUrl Uploaded of " + finalI + " to firestore " + imgUrl.toString());
                                        } else {
                                            Log.d(TAG, task.getException().getMessage().toString());
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        progressBar.dismiss();
                        String ErrorMsg = task.getException().getMessage().toString();
                        Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, ErrorMsg);
                    }
                }
            });
        }
        progressBar.dismiss();
        Toast.makeText(AddProduct.this, "Image upload may take some time", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddProduct.this, LandingPageActivity.class));
    }

    public List<String> generateKeywords(String name) {
        List<String> keywords = new ArrayList<>();
        name = name.replaceAll(" ", "");
        name = name.trim().toLowerCase(Locale.ROOT);
        Log.d(TAG, name);
        Log.d(TAG, String.valueOf(name.length()));

        for (int i = 0; i < name.length(); i++) {
            for (int j = i; j < name.length(); j++) {
                keywords.add(name.substring(i, j + 1));
            }
        }
        return keywords;
    }
}
