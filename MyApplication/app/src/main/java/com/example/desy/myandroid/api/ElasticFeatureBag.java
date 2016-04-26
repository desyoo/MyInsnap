package com.example.desy.myandroid.api;

import java.util.HashSet;
import java.util.Set;


public class ElasticFeatureBag {
    private Set<Integer> bag;

    public ElasticFeatureBag() {
        this.bag = new HashSet<>();
    }

    public Feature addFeatureByKey(String featureKey) {
        Feature feature = ElasticFeatureBagManager.getInstance().evaluateFeatureIndex(featureKey);
        this.bag.add(feature.getIndex());

        return feature;
    }

    public Feature addFeatureByKey(String featureKey, String dynamicType) {
        Feature feature = ElasticFeatureBagManager.getInstance().evaluateFeatureIndex(featureKey);

        this.bag.add(feature.getIndex());

        return new DynamicFeature(feature, dynamicType);
    }

    public Set<Integer> getBag() {
        return bag;
    }
}
