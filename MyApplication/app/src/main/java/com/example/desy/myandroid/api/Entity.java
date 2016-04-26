package com.example.desy.myandroid.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Entity {
    private String localIdentifier;
    private JSONObject metadata;
    private ElasticFeatureBag elasticFeatureBag;

    public String getLocalIdentifier() {
        return localIdentifier;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public ElasticFeatureBag getElasticFeatureBag() {
        return elasticFeatureBag;
    }

    public Entity(JSONObject phAsset) throws JSONException {
        this.localIdentifier = phAsset.getString("localIdentifier");
        this.metadata = phAsset;
        this.elasticFeatureBag = new ElasticFeatureBag();
    }

    @Override
    public String toString() {
        return this.localIdentifier;
    }
}