package com.klimovmax.myapplication21;

import java.io.Serializable;

public class NewPost implements Serializable {
    private String imageId;
    private String imageId2;
    private String imageId3;
    private String title;
    private String price;
    private String tel;
    private String disc;
    private String key;
    private String uid;
    private String time;
    private String cat;
    private String email;
    private String total_views;
    private String total_emails;
    private String total_calls;

    public String getEmail() {
        return email;
    }

    public String getTotal_emails() {
        return total_emails;
    }

    public void setTotal_emails(String total_emails) {
        this.total_emails = total_emails;
    }

    public String getTotal_calls() {
        return total_calls;
    }

    public void setTotal_calls(String total_calls) {
        this.total_calls = total_calls;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageId2() {
        return imageId2;
    }

    public void setImageId2(String imageId2) {
        this.imageId2 = imageId2;
    }

    public String getImageId3() {
        return imageId3;
    }

    public void setImageId3(String imageId3) {
        this.imageId3 = imageId3;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
