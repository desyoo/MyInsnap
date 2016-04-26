package com.example.desy.myandroid.api;


public class Feature {
    private String key;
    private Integer index;

    public String getKey() {
        return key;
    }

    public Integer getIndex() {
        return index;
    }

    public Feature(String key, Integer index) {
        this.key = key;
        this.index = index;
    }

    @Override
    public String toString() {
        return key + ":" + index;
    }
}
