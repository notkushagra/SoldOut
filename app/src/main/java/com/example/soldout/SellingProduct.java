package com.example.soldout;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SellingProduct {

    String buyerId,desc,name,price,sellerId,productId;
    ArrayList<String>images;
    ArrayList<String>tags;
    long visitCount;
    boolean soldStatus;

    public SellingProduct(){}

    public SellingProduct(String buyerId, String desc, String name, String price, String sellerId, String productId, ArrayList<String> images, ArrayList<String> tags, long visitCount, boolean soldStatus) {
        this.buyerId = buyerId;
        this.desc = desc;
        this.name = name;
        this.price = price;
        this.sellerId = sellerId;
        this.productId = productId;
        this.images = images;
        this.tags = tags;
        this.visitCount = visitCount;
        this.soldStatus = soldStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(long visitCount) {
        this.visitCount = visitCount;
    }

    public boolean isSoldStatus() {
        return soldStatus;
    }

    public void setSoldStatus(boolean soldStatus) {
        this.soldStatus = soldStatus;
    }

}
