package com.example.desy.myandroid.api;

/**
 * Created by iman on 7/15/15.
 */
public class Facet {
    private String id;
    private String title;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public Facet(String id, String title, int count) {
        this.id = id;
        this.title = title;
        this.count = count;
    }

    @Override
    public String toString() {
        return String.format("id:%s title:%s count:%d", this.id, this.title, this.count);
    }
}
