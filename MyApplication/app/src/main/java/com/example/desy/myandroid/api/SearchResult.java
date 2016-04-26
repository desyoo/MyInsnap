package com.example.desy.myandroid.api;

import java.util.ArrayList;
import java.util.HashSet;


public class SearchResult {
    private ArrayList<SimilarityResult> results;
    private HashSet<Facet> facets;

    public SearchResult(ArrayList<SimilarityResult> results) {
        this.results = results;
    }

    public SearchResult(ArrayList<SimilarityResult> results, HashSet<Facet> facets) {
        this(results);
        this.facets = facets;
    }

    public HashSet<Facet> getFacets() {
        return facets;
    }

    public ArrayList<SimilarityResult> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return String.format("%d results were returned", this.results.size());
    }
}
