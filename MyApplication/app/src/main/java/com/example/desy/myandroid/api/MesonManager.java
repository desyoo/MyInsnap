package com.example.desy.myandroid.api;

import android.content.Context;
import android.util.Log;

import com.example.desy.myandroid.Utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by desy on 4/20/16.
 */
public class MesonManager {
    private static final String TAG = MesonManager.class.getSimpleName();
    private ArrayList<Entity> entities = null;
    private HashMap<String, Integer> staticFacetCache = null;
    private HashMap<String, HashMap<String, Facet>> dynamicFacetCache = null;

    private HashMap<String, ArrayList<SimilarityResult>> cachedSearches = null;

    private static final Object mLock = new Object();
    private static MesonManager mInstance;
    private Context mAppContext;


    public static MesonManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new MesonManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }


    private MesonManager(Context context) {
        mAppContext = context;
        this.entities = new ArrayList<>();
        this.staticFacetCache = new HashMap<>();
        this.dynamicFacetCache = new HashMap<>();
        this.cachedSearches = new HashMap<>();

    }


    private void trackStaticFacets(String key) {
        if (!this.staticFacetCache.containsKey("key")) {
            this.staticFacetCache.put(key, new Integer(0));
        }

        int val = this.staticFacetCache.get(key);
        this.staticFacetCache.put(key, val + 1);
    }


    // loop through all the entities features and extract facets
    private void trackDynamicFacets(String facetID, String dynamicType) {

        if (!this.dynamicFacetCache.containsKey(dynamicType)) {
            this.dynamicFacetCache.put(dynamicType, new HashMap<String, Facet>());
        }

        HashMap<String, Facet> typeSet = this.dynamicFacetCache.get(dynamicType);

        if (typeSet != null) {
            if (!typeSet.containsKey(facetID)) {
                String facetName = facetID.endsWith("Enthusiast") ? facetID.replace("Enthusiast", "") : facetID;
                facetName = StringEscapeUtils.unescapeHtml4(facetName).trim();
                typeSet.put(facetID, new Facet(facetID, facetName, 1));
            } else {
                Facet facet = typeSet.get(facetID);
                facet.setCount(facet.getCount() + 1);
                typeSet.put(facetID, facet);
            }
        }
    }

    private void extractFacets(Entity entity, ArrayList<Feature> features) {
        for (Feature feature : features) {
            if (!this.cachedSearches.containsKey(feature.getKey())) {
                this.cachedSearches.put(feature.getKey(), new ArrayList<SimilarityResult>());
            }
            this.cachedSearches.get(feature.getKey()).add(new SimilarityResult(entity));
        }

        // always add AllTime
        if (!this.cachedSearches.containsKey(Utils.Constants.AssetFeatureKey.ALLTIME)) {
            this.cachedSearches.put(Utils.Constants.AssetFeatureKey.ALLTIME, new ArrayList<SimilarityResult>());
        }

        this.cachedSearches.get(Utils.Constants.AssetFeatureKey.ALLTIME).add(new SimilarityResult(entity));
    }

    private void extractFacetsForRealtime(Entity entity, ArrayList<Feature> features) {
        for (Feature feature : features) {
            if (!this.cachedSearches.containsKey(feature.getKey())) {
                this.cachedSearches.put(feature.getKey(), new ArrayList<SimilarityResult>());
            }
            this.cachedSearches.get(feature.getKey()).add(new SimilarityResult(entity));
        }
    }


    public void addAssets(ArrayList<JSONObject> assets) {
        for (JSONObject asset : assets) {
            try {
                Entity entity = new Entity(asset.getJSONObject("PHAsset"));
                ArrayList<Feature> features = new ArrayList<>();

                int pixelHeight = entity.getMetadata().has("pixelHeight") ? entity.getMetadata().getInt("pixelHeight") : -1;
                int pixelWidth = entity.getMetadata().has("pixelWidth") ? entity.getMetadata().getInt("pixelWidth") : -1;

                boolean hasValidDimensions = pixelHeight > -1 && pixelWidth > -1;

                int mediaType = entity.getMetadata().getInt("mediaType");
                if (mediaType == 1) {
                    features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.PHOTO));
                    trackStaticFacets(Utils.Constants.AssetFeatureKey.PHOTO);

                    //Log.d(TAG, String.format("height:" + pixelHeight + " width:" + pixelWidth));

                    if (hasValidDimensions && (pixelWidth > pixelHeight)) {
                        features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.LANDSCAPE));
                    } else {
                        features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.PORTRIAT));
                    }


                }
                if (mediaType == 2) {
                    features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.VIDEO));
                    trackStaticFacets(Utils.Constants.AssetFeatureKey.VIDEO);
                }

                String bucketDisplayName = entity.getMetadata().getString("bucketDisplayName");
                if (hasValidDimensions && bucketDisplayName.equals("Camera") && ((pixelHeight == 1280 && pixelWidth == 960) || (pixelHeight == 720 && pixelWidth == 1280))) {
                    features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.SELFIE));
                    trackStaticFacets(Utils.Constants.AssetFeatureKey.SELFIE);
                } else {
                    entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.NON_SELFIE);
                }

                if (hasValidDimensions && (pixelHeight == pixelWidth)) {
                    features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.SQUARE));
                    trackStaticFacets(Utils.Constants.AssetFeatureKey.SQUARE);
                }


                if (bucketDisplayName.equals("Screenshots")) {
                    features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.SCREENSHOT));
                    trackStaticFacets(Utils.Constants.AssetFeatureKey.SCREENSHOT);
                }

                if (bucketDisplayName.equals("Download")) {
                    features.add(entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.DOWNLOAD));
                    trackStaticFacets(Utils.Constants.AssetFeatureKey.DOWNLOAD);
                }


                // this is on for every entity
                entity.getElasticFeatureBag().addFeatureByKey(Utils.Constants.AssetFeatureKey.ALLTIME);

                extractFacets(entity, features);
                entities.add(entity);
            } catch (JSONException e) {
                Log.i("Meson", e.getMessage());
            }
        }

        Log.i("Meson", ElasticFeatureBagManager.getInstance().getAllFeatures().toString());
    }


    public SearchResult search(ArrayList<Facet> criteria) {
        ArrayList<SimilarityResult> results = doSearch(criteria);
        HashSet<Facet> facets = getFacetsForThisSearch(criteria);

        return new SearchResult(results, facets);
    }

    private ArrayList<SimilarityResult> doSearch(ArrayList<Facet> criteria) {
        ArrayList<SimilarityResult> results = new ArrayList<SimilarityResult>();

        if (criteria.size() == 0) {
            return this.cachedSearches.get(Utils.Constants.AssetFeatureKey.ALLTIME);
        }

        return this.cachedSearches.get(criteria.get(0).getId());
    }

    private HashSet<Facet> getFacetsForThisSearch(ArrayList<Facet> criteria) {
        HashSet<Facet> facets = new HashSet<Facet>();
        facets.add(new Facet("IsSelfie", "Selfie", 1));
        facets.add(new Facet("IsHoliday", "Holiday", 1));
        return facets;
    }


}
