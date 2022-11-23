//package com.example.soldout;
//
//public class temp {
////    sellBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                progressBar.show();
////
////                //get data from entry points
////                final String productNameTxt = productName.getText().toString();
////                final String productDescTxt = productDesc.getText().toString();
////                final String productPriceTxt = productPrice.getText().toString();
////
////                //imageUri comes from add image button
////                if (ImageUri == null) {
////                    progressBar.dismiss();
////                    Toast.makeText(AddProduct.this, "Attach at least one image", Toast.LENGTH_SHORT).show();
////                } else if (productNameTxt.trim().equals("") || productDescTxt.trim().equals("") || productPriceTxt.trim().equals("")) {
////                    progressBar.dismiss();
////                    Toast.makeText(AddProduct.this, "Enter all the details", Toast.LENGTH_SHORT).show();
////                } else {
////                    //upload the file to cloud storage -> store the url and all to db -> make entries in two collection
////
////                    //giving the uploaded file a name
////                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
////                    Date now = new Date();
////                    String fileName = formatter.format(now);
////                    String filePath = "images/" + currentUser.getUid() + "/" + productNameTxt.toLowerCase() + "/" + fileName;
////                    // now we get reference to the area our file is uploaded in
////                    storageRef = storage.getReference(filePath);
////
////                    //uploading file to cloud storage
////                    storageRef.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
////                        @Override
////                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
////                            if (task.isSuccessful()) {
////                                Toast.makeText(AddProduct.this, "Image Successfully Uploaded", Toast.LENGTH_SHORT).show();
////                                storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
////                                    @Override
////                                    public void onComplete(@NonNull Task<Uri> task) {
////                                        if (task.isSuccessful()) {
////                                            ImageUrl = task.getResult().toString();
////                                            images.add(ImageUrl);
////                                            Log.d(TAG, "added URI");
////
////                                            //save Instance of product in firestore
////                                            Map<String, Object> product = new HashMap<>();
////                                            boolean intiSoldStatus = false;
////                                            int initVisitCount = 0;
////                                            List<String> tags = new ArrayList<>();
////                                            final String userIDTxt = currentUser.getUid().toString();
////
////                                            product.put("name", productNameTxt);
////                                            product.put("desc", productDescTxt);
////                                            product.put("price", productPriceTxt);
////                                            product.put("buyerId", "");
////                                            product.put("sellerId", userIDTxt);
////                                            product.put("soldStatus", intiSoldStatus);
////                                            product.put("visitCount", initVisitCount);
////                                            product.put("tags", tags);
////                                            product.put("images", images);
////
////                                            db.collection("sellingProducts")
////                                                    .add(product)
////                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
////                                                        @Override
////                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
////                                                            if (task.isSuccessful()) {
////                                                                Log.d(TAG, task.getResult().toString());
////                                                                Log.d(TAG, task.getResult().getId());
////                                                                Log.d(TAG, "check");
////                                                                final String productId = task.getResult().getId();
////
////                                                                //save image in cloud storage
////
////                                                                storageRef.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
////                                                                    @Override
////                                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
////                                                                        if (task.isSuccessful()) {
////                                                                            imgUplaoded.setImageURI(null);
////                                                                            Toast.makeText(AddProduct.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
////
////                                                                            db.collection("users").document(userIDTxt).update("sellingProducts", FieldValue.arrayUnion(productId)).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                @Override
////                                                                                public void onComplete(@NonNull Task<Void> task) {
////                                                                                    if (task.isSuccessful()) {
////                                                                                        progressBar.dismiss();
////                                                                                        startActivity(new Intent(AddProduct.this, MainActivity.class));
////                                                                                    } else {
////                                                                                        String ErrorMsg = task.getException().getMessage().toString();
////                                                                                        Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
////                                                                                        Log.d(TAG, ErrorMsg);
////                                                                                    }
////                                                                                }
////                                                                            });
////
////                                                                        } else {
////                                                                            progressBar.dismiss();
////                                                                            String ErrorMsg = task.getException().getMessage().toString();
////                                                                            Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
////                                                                            Log.d(TAG, ErrorMsg);
////                                                                        }
////                                                                    }
////                                                                });
////                                                            } else {
////                                                                progressBar.dismiss();
////                                                                Log.d(TAG, "add to db fails");
////                                                            }
////                                                        }
////                                                    });
////
////
////                                        } else {
////                                            Log.d(TAG, task.getException().getMessage().toString());
////                                        }
////                                    }
////                                });
////                            } else {
////                                progressBar.dismiss();
////                                String ErrorMsg = task.getException().getMessage().toString();
////                                Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
////                                Log.d(TAG, ErrorMsg);
////                            }
////                        }
////                    });
////                }
////            }
////        });
////
////}
//
//
//auctionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.show();
//
//                final String productNameTxt = productName.getText().toString();
//                final String productDescTxt = productDesc.getText().toString();
//                final String productPriceTxt = productPrice.getText().toString();
//                //imageUri comes from add image button
//
//                if (ImageUri == null) {
//                    progressBar.dismiss();
//                    Toast.makeText(AddProduct.this, "Attach at least one image", Toast.LENGTH_SHORT).show();
//                } else if (productNameTxt.trim().equals("") || productDescTxt.trim().equals("") || productPriceTxt.trim().equals("")) {
//                    progressBar.dismiss();
//                    Toast.makeText(AddProduct.this, "Enter all the details", Toast.LENGTH_SHORT).show();
//                } else {
//                    //save instance in firestore
//                    Map<String, Object> product = new HashMap<>();
//                    boolean intiSoldStatus = false;
//                    int initVisitCount = 0;
//                    List<String> tags = new ArrayList<>();
//                    List<String> images = new ArrayList<>();
//                    String currentUserId = currentUser.getUid().toString();
//
//                    //giving the uploaded file a name
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
//                    Date now = new Date();
//                    String fileName = formatter.format(now);
//                    String filePath = "images/" + currentUser.getUid() + "/" + productNameTxt.toLowerCase() + "/" + fileName;
//                    images.add(filePath);
//                    storageRef = storage.getReference(filePath);
//
//                    product.put("name", productNameTxt);
//                    product.put("desc", productDescTxt);
//                    product.put("highestBid", productPriceTxt);
//                    product.put("highestBidderId", "");
//                    product.put("sellerId", currentUserId);
//                    product.put("soldStatus", intiSoldStatus);
//                    product.put("visitCount", initVisitCount);
//                    product.put("tags", tags);
//                    product.put("images", images);
//
//                    //adding user to Users
//                    db.collection("auctionProducts")
//                            .add(product)
//                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentReference> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d(TAG, task.getResult().toString());
//                                        Log.d(TAG, task.getResult().getId());
//                                        Log.d(TAG, "check");
//                                        final String productId = task.getResult().getId();
//
//                                        //save image in cloud storage
//
//                                        storageRef.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    imgUplaoded.setImageURI(null);
//                                                    Toast.makeText(AddProduct.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
//                                                    db.collection("users").document(currentUserId).update("auctionProducts", FieldValue.arrayUnion(productId)).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                progressBar.dismiss();
//                                                                startActivity(new Intent(AddProduct.this, MainActivity.class));
//                                                            } else {
//                                                                progressBar.dismiss();
//                                                                String ErrorMsg = task.getException().getMessage();
//                                                                Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
//                                                                Log.d(TAG, ErrorMsg);
//                                                            }
//                                                        }
//                                                    });
//                                                } else {
//                                                    progressBar.dismiss();
//                                                    String ErrorMsg = task.getException().getMessage();
//                                                    Toast.makeText(AddProduct.this, ErrorMsg, Toast.LENGTH_SHORT).show();
//                                                    Log.d(TAG, ErrorMsg);
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        progressBar.dismiss();
//                                        Log.d(TAG, "add to db fails");
//                                    }
//                                }
//                            });
//                }
//            }
//        });