package com.example.soldout;

import java.util.ArrayList;

public class AuctionProduct {

    String highestBidderId, desc, name, price, sellerId, productId;
    ArrayList<String> images;
    ArrayList<String> tags;
    long visitCount;
    boolean soldStatus;

    public AuctionProduct(){}

    public AuctionProduct(String highestBidderId, String desc, String name, String price, String sellerId, String productId, ArrayList<String> images, ArrayList<String> tags, long visitCount, boolean soldStatus) {
        this.highestBidderId = highestBidderId;
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

    public String getHighestBidderId() {
        return highestBidderId;
    }

    public void setHighestBidderId(String highestBidderId) {
        this.highestBidderId = highestBidderId;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
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
