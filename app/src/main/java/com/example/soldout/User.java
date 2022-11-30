package com.example.soldout;

import java.util.List;

public class User {
    String fullname,room_no,phone,userID;
    List<String> auctionProducts,sellingProducts;
    List<Notification> notifications;
    User(){}

    public User(String fullname, String room_no, String phone, String userID, List<String> auctionProducts, List<String> sellingProducts, List<Notification> notifications) {
        this.fullname = fullname;
        this.room_no = room_no;
        this.phone = phone;
        this.userID = userID;
        this.auctionProducts = auctionProducts;
        this.sellingProducts = sellingProducts;
        this.notifications = notifications;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<String> getAuctionProducts() {
        return auctionProducts;
    }

    public void setAuctionProducts(List<String> auctionProducts) {
        this.auctionProducts = auctionProducts;
    }

    public List<String> getSellingProducts() {
        return sellingProducts;
    }

    public void setSellingProducts(List<String> sellingProducts) {
        this.sellingProducts = sellingProducts;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
