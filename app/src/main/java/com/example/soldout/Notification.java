package com.example.soldout;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    String title, text;
    Timestamp timeOfGen;
    boolean forSale;
    String productId;

    public Notification() {
    }

    public Notification(String title, String text, Timestamp timeOfGen, boolean forSale, String productId) {
        this.title = title;
        this.text = text;
        this.timeOfGen = timeOfGen;
        this.forSale = forSale;
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimeOfGen() {
        return timeOfGen;
    }

    public void setTimeOfGen(Timestamp timeOfGen) {
        this.timeOfGen = timeOfGen;
    }

    public boolean isForSale() {
        return forSale;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public HashMap<String, Object> toMapObject() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        hashMap.put("title", this.title);
        hashMap.put("text", this.text);
        hashMap.put("timeOfGen", this.timeOfGen);
        hashMap.put("forSale", this.forSale);
        hashMap.put("productId", this.productId);

        return hashMap;
    }
}
