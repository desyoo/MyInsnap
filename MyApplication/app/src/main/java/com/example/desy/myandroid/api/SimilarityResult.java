package com.example.desy.myandroid.api;


public class SimilarityResult {
    private Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public SimilarityResult(Entity entity) {
        this.entity = entity;
    }
}
