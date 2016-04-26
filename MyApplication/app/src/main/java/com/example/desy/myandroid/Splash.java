package com.example.desy.myandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.desy.myandroid.api.Facet;
import com.example.desy.myandroid.api.MesonManager;
import com.example.desy.myandroid.api.SearchResult;
import com.example.desy.myandroid.api.SimilarityResult;

import java.util.ArrayList;

/**
 * Created by desy on 4/20/16.
 */
public class Splash extends AppCompatActivity {
    private final String TAG = Splash.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private String savedEnvironment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.splashProgressBar);
        //progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);


        Runnable loadingScreen = new Runnable() {
            @Override
            public void run() {

            }
        };



        if (Utils.isNetworkAvailable(getApplicationContext())) {
            onInterestProfileReady();
        } else {
            displayAlert();
        }

        mSharedPreference = this.getSharedPreferences("adminServer", Context.MODE_PRIVATE);
        savedEnvironment = mSharedPreference.getString("savedEnvironment", "default");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("splash", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("splash", "onRestart");
    }



    public void displayAlert() {
        new AlertDialog.Builder(this).setMessage(R.string.no_network)
                .setTitle("Network Error")
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                .show();
    }

    private ArrayList<SimilarityResult> getSimilarResultWithFacetAs(String facetType){
        ArrayList<Facet> criteria = new ArrayList<>();
        Facet emptySearch = new Facet(Utils.Constants.AssetFeatureKey.ALLTIME, facetType, 1);//TODO : 1st param
        criteria.add(emptySearch);

        SearchResult searchResult = MesonManager.getInstance(getApplicationContext()).search(criteria);

        return searchResult.getResults();
    }


    public void onInterestProfileReady() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);

    }



}
