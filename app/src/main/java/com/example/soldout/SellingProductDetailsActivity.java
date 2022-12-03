package com.example.soldout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellingProductDetailsActivity extends AppCompatActivity {

    final String TAG = "SellingProductDetails";
    String sellerId;
    FirebaseFirestore db;

    private ImageSlider imageSlider;

    TextView productName;
    TextView productDesc;
    TextView productPrice;
    TextView sellerInfo;
    String productId;
    Button buyNowBtn;
    FirebaseUser currentUser;
    String userId;
    String userName;
    String productDescTxt, productTitle;
    boolean soldStatus;
    List<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_product_details);

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

        //Recieve prodcutID from intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("EXTRA_PRODUCT_ID");
        Log.d(TAG, productId);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userId = currentUser.getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> user = new HashMap<>();
                        user = doc.getData();
                        userName = user.get("fullname").toString();
                    }
                }
            }
        });

        imageSlider = findViewById(R.id.imageSlider);
        productDesc = findViewById(R.id.productDesc);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        sellerInfo = findViewById(R.id.sellerInfo);
        buyNowBtn = findViewById(R.id.buyNowBtn);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        //increase of visitCount by 1;
        db.collection("sellingProducts").document(productId).update("visitCount", FieldValue.increment(1));

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //buyer check
                if (sellerId.equals(userId)) {
                    Toast.makeText(getApplicationContext(), "You shouldn't bid on or buy your own products :)", Toast.LENGTH_SHORT).show();
                    return;
                }


                db.collection("sellingProducts").document(productId).update("soldStatus", true);
                db.collection("sellingProducts").document(productId).update("buyerId", userId);

//                String title, text;
//                Timestamp timeOfGen;
//                boolean forSale;
//                String productId;

                // add notification in seller and buyer

                String text = "Your product - \"" + productTitle + "\" was bought by " + userName + ".";
                Timestamp timestamp = Timestamp.now();
                Notification notification = new Notification("Bought", text, timestamp, true, productId);
                HashMap<String, Object> notifEntry = notification.toMapObject();
                db.collection("users").document(sellerId).update("notifications", FieldValue.arrayUnion(notifEntry));


                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        finish();
                        startActivity(getIntent());
                        return true;
                    }
                });

            }
        });

        db.collection("sellingProducts").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> product = new HashMap<>();
                        product = doc.getData();
                        productDescTxt = product.get("desc").toString();
                        productTitle = product.get("name").toString();
                        soldStatus = (boolean) product.get("soldStatus");
                        images = (List<String>) product.get("images");

                        for (int i = 0; i < images.size(); i++) {
                            slideModels.add(new SlideModel(images.get(i).toString(), ScaleTypes.CENTER_INSIDE));
                        }
                        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);

                        productName.setText(productTitle);
                        productDesc.setText(productDescTxt);


                        String bidTag = (String) product.get("price");
                        String productPriceTxt;

                        if (soldStatus) {
                            productPriceTxt = "SOLD OUT!";
                            Context context = getApplicationContext();
                            CharSequence text = "Item has been sold";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            buyNowBtn.getBackground().setAlpha(128);
                            buyNowBtn.setClickable(false);
                        } else if (bidTag != null) {
                            productPriceTxt = "Rs. " + bidTag;
                        } else {
                            productPriceTxt = "No Bids Yet";
                        }
                        sellerId = product.get("sellerId").toString();
                        productPrice.setText(productPriceTxt);

                        db.collection("users").document(sellerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        Map<String, Object> seller = new HashMap<>();
                                        seller = doc.getData();
                                        final String sellerData = "Seller Details \n" + seller.get("fullname") + "\n" + seller.get("room no").toString() + "\n" + seller.get("phone").toString();
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