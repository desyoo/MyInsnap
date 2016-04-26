package com.example.desy.myandroid.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.desy.myandroid.R;
import com.example.desy.myandroid.Utils;
import com.example.desy.myandroid.adapter.SmartFragmentStatePagerAdapter;
import com.example.desy.myandroid.api.Entity;
import com.example.desy.myandroid.api.Facet;
import com.example.desy.myandroid.api.MesonManager;
import com.example.desy.myandroid.api.NeonManager;
import com.example.desy.myandroid.api.SearchResult;
import com.example.desy.myandroid.api.SimilarityResult;
import com.example.desy.myandroid.model.Picture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RecomFragment extends Fragment {
    private String TAG = RecomFragment.class.getSimpleName();
    private ViewPager pager;
    private RecomPagerAdapter mRecomPagerAdapter;
    private SharedPreferences mSharedResutPreference;
    private ArrayList<SimilarityResult> imageIdList;
    private static ArrayList<JSONObject> assets;
    private LruCache<String, Bitmap> mMemoryCache;
    private final static String MEMORY_TAG = "Memory";
    private static Picture picture;
    private static Picture picture2;

    public RecomFragment() {
    }


    private void initMemoryCache(){
        /*
         * Get Max available VM memeory, exceeding this amount will throw an
         * outOfMemory exception. Stored in kilobytes as Lrucache takes an
         * int in its constructor.
         * */
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
//        Using 1/8 of the available memory for this memory cache
        final int cacheSize = maxMemory/8;
        Log.v(MEMORY_TAG, "Cache size : " + cacheSize);
        mMemoryCache = new LruCache<String ,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //The cache size will be measured in KB rather than number of items.
                return bitmap.getByteCount()/1024;
            }
        };
    }

    public static RecomFragment newInstance(Context context) {
        RecomFragment fragment = new RecomFragment();
        assets = NeonManager.getInstance(context).getAssets();
        if (assets != null) {
            MesonManager.getInstance(context).addAssets(assets);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMemoryCache();
        getSinglePicture();
        getSecondPicture();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recom, container, false);
        pager = (ViewPager) rootView.findViewById(R.id.fragment_recom_pager);
        mRecomPagerAdapter = new RecomPagerAdapter(getChildFragmentManager());
        pager.setAdapter(mRecomPagerAdapter);

        // Give the TabLayout the ViewPager
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs2);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                int tabLayoutWidth = tabLayout.getWidth();

                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int deviceWidth = metrics.widthPixels;

                if (tabLayoutWidth < deviceWidth - 150) {
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    tabLayout.setMinimumWidth(deviceWidth);

                } else {
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        });

        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private ArrayList<SimilarityResult> getSimilarResultWithFacetAs(String facetType){
        ArrayList<Facet> criteria = new ArrayList<>();
        Facet emptySearch = new Facet(Utils.Constants.AssetFeatureKey.ALLTIME, facetType, 1);//TODO : 1st param
        criteria.add(emptySearch);

        SearchResult searchResult = MesonManager.getInstance(getContext()).search(criteria);

        return searchResult.getResults();
    }


    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void getSinglePicture() {
        imageIdList = getSimilarResultWithFacetAs(Utils.Constants.AssetFeatureKey.PHOTO);
        Entity entity = imageIdList.get(0).getEntity();
        int length = getContext().getResources().getDisplayMetrics().widthPixels/2;
        int orientation = 0;
        try {
            if (entity.getMetadata().getInt("mediaType") == 1) {
                orientation = entity.getMetadata().getInt("orientation");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String id = entity.getLocalIdentifier();
        int mediaType = Utils.Constants.MediaType.IMAGE;
        try{
            mediaType = entity.getMetadata().getInt("mediaType");
        }catch (JSONException e){
            e.printStackTrace();
        }
        picture = new Picture(id,mediaType,length, orientation);
    }

    public void getSecondPicture() {
        imageIdList = getSimilarResultWithFacetAs(Utils.Constants.AssetFeatureKey.PHOTO);
        Entity entity = imageIdList.get(1).getEntity();
        int length = getContext().getResources().getDisplayMetrics().widthPixels/2;
        int orientation = 0;
        try {
            if (entity.getMetadata().getInt("mediaType") == 1) {
                orientation = entity.getMetadata().getInt("orientation");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String id = entity.getLocalIdentifier();
        int mediaType = Utils.Constants.MediaType.IMAGE;
        try{
            mediaType = entity.getMetadata().getInt("mediaType");
        }catch (JSONException e){
            e.printStackTrace();
        }
        picture2 = new Picture(id,mediaType,length, orientation);
    }


    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    private static class RecomPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 2;
        private String TAG = RecomPagerAdapter.class.getSimpleName();
        private Context myContext;
        private String[] imageResId = new String[] {
                "AM",
                "PM"
        };

        public RecomPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            //VirtualTab virtualTab = VirtualTabManager.getInstance().getVirtualTabs().get(position);
            //return RecommendationFragmentFactory.createRecommendationFragment(virtualTab.getRecommendationType(),position);

            switch (position) {
                case 0:
                    return RecomItemOneFrag.newInstance(picture);
                case 1:
                    return RecomItemTwoFrag.newInstance(picture2);
                default:
                    return null;
            }

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return imageResId[position];
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
