package com.example.desy.myandroid.api;

/**
 * Created by desy on 8/17/15.
 */
public class DynamicFeature extends Feature {
    private String dynamicType;

    public DynamicFeature(Feature feature, String dynamicType) {
        super(feature.getKey(), feature.getIndex());

        this.dynamicType = dynamicType;
    }
}
