package com.example.desy.myandroid.manager;

import android.content.Context;

import com.example.desy.myandroid.api.Facet;

import java.util.ArrayList;

/**
 * Created by desy on 4/23/16.
 */
public class FacetManager {
    private static final String TAG = FacetManager.class.getSimpleName();
    private static final Object mLock = new Object();
    private static FacetManager mInstance;

    private Context context;
    private ArrayList<Facet> facets;

    public static FacetManager getmInstance (Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new FacetManager(context);
            }
            return  mInstance;
        }
    }

    public FacetManager (Context context) {
        this.context = context;
        this.facets = new ArrayList<>();
    }



}
