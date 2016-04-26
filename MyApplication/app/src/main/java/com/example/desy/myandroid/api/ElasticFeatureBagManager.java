package com.example.desy.myandroid.api;

import java.util.HashMap;
import java.util.Map;


public class ElasticFeatureBagManager {
    private static ElasticFeatureBagManager ourInstance = new ElasticFeatureBagManager();
    private Map<String, Feature> allFeatures = null;
    private int nextAvailableIndex = 0;
    public static ElasticFeatureBagManager getInstance() {
        return ourInstance;
    }

    private ElasticFeatureBagManager() {
        this.allFeatures = new HashMap<>();
    }

    public Feature evaluateFeatureIndex (String featureKey) {
        if (this.allFeatures.containsKey(featureKey)) {
            return allFeatures.get(featureKey);
        }
        else {
            Feature feature = new Feature(featureKey, nextAvailableIndex++);
            this.allFeatures.put(featureKey, feature);
            return feature;
        }
    }

    public Map<String, Feature> getAllFeatures() {
        return allFeatures;
    }
}
