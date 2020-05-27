package com.example.findmypeople;

import com.google.firebase.firestore.IgnoreExtraProperties;

public class User {
    private String name, uid;


    public User(){

    }

    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
