package com.example.desy.myandroid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.desy.myandroid.adapter.SmartFragmentStatePagerAdapter;
import com.example.desy.myandroid.api.BaryonManager;
import com.example.desy.myandroid.api.MesonManager;
import com.example.desy.myandroid.api.NeonManager;
import com.example.desy.myandroid.fragment.FavorFragment;
import com.example.desy.myandroid.fragment.MainFragment;
import com.example.desy.myandroid.fragment.RecomFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private ArrayList<JSONObject> assets;
    private MainPagerAdapter mainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaryonManager.getInstance(getApplicationContext()).init();
        BaryonManager.getInstance(getApplicationContext()).initCommonPayload();
        readExternalStorage();
        askUserPermission();

        ViewPager vpPager = (ViewPager) findViewById(R.id.fragment_main_pager);
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(mainPagerAdapter);

        // Give the TabLayout the ViewPager
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                int tabLayoutWidth = tabLayout.getWidth();

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
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



    }


    private void askUserPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the external storage is required for this app to display your photos.")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeRequest();
            }
        }
    }


    private void readExternalStorage() {
        NeonManager.getInstance(getApplicationContext()).init();

        assets = NeonManager.getInstance(getApplicationContext()).getAssets();

        if (assets != null) {
            Log.d("Assets", assets.toString());
        }
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ArrayList<JSONObject> assets = NeonManager.getInstance(getApplicationContext()).getAssets();
                    MesonManager.getInstance(getApplicationContext()).addAssets(assets);
                    Log.i(TAG, "external accepted");
                } else {
                    Log.i(TAG, "external not accepted");
                }
                return;
            }
        }
    }



    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    private static class MainPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 3;
        private String TAG = MainPagerAdapter.class.getSimpleName();
        private Context myContext;
        private int[] imageResId = new int[] {
                R.drawable.ic_home,
                R.drawable.ic_photos,
                R.drawable.ic_pages
        };

        public MainPagerAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            myContext = context;
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
                    return MainFragment.newInstance();
                case 1:
                    return RecomFragment.newInstance(myContext);
                case 2:
                    return FavorFragment.newInstance("1","1");
                default:
                    return null;
            }

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            //return VirtualTabManager.getInstance().getVirtualTabs().get(position).getTitle();
            Drawable image = ContextCompat.getDrawable(myContext, imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


}
