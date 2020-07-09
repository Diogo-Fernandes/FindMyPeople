package com.example.findmypeople;

public class NotifsModel {
    private String child_name;
    private String content;
    private String date;

    public NotifsModel() {
    }

    public NotifsModel(String child_name, String content, String date) {
        this.child_name = child_name;
        this.content = content;
        this.date = date;
    }

    public String getChild_name() {
        return child_name;
    }

    public void setChild_name(String child_name) {
        this.child_name = child_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
