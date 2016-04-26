package com.example.desy.myandroid.api;

import android.accounts.Account;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by desy on 4/19/16.
 */
public abstract class BaseAPIManager {
    public static final String SyncedAssets = "SyncedAssets";
    protected String environment = null;
    protected String serverUrl = null;
    protected String realtimeServerUrl = null;
    protected HashSet permissions = null;
    protected String realtimeApiVersion = null;
    protected String loggingApiVersion = null;
    protected boolean isSyncAssetsDelta = true;
    protected Context mAppContext = null;
    protected Account account = null;

    public abstract void addPermissionChecks();

    public void init() {
        try {
            ApplicationInfo applicationInfo = mAppContext.getPackageManager().getApplicationInfo(mAppContext.getPackageName(), PackageManager.GET_META_DATA);

            //Log.i("BaseAPIManager", realtimeServerUrl);
        }
        catch (PackageManager.NameNotFoundException exception) {
            Log.e("BaseAPIManager", exception.getMessage());
        }


        this.permissions = new HashSet();
        // get the current app's package name
        String packageName = mAppContext.getPackageName();

        PackageManager packageManager = mAppContext.getPackageManager();

        try {
            PackageInfo pi = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            if(pi.requestedPermissions == null || pi.requestedPermissions.length == 0) {
                Log.i("BaseAPIManager", "app has no permissions");
            }
            else {
                Log.i("BaseAPIManager","app has " + pi.requestedPermissions.length + " requested permissions");

                for (int i = 0; i < pi.requestedPermissions.length; ++i) {
                    Log.d("BaseAPIManager", "permission: " + pi.requestedPermissions[i]);
                    this.permissions.add(pi.requestedPermissions[i]);
                }
            }

            this.addPermissionChecks();
        }
        catch(PackageManager.NameNotFoundException exception) {
            Log.e("BaseAPIManager", exception.getMessage());
        }
    }

}
