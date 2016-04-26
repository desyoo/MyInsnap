package com.example.desy.myandroid.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by desy on 4/19/16.
 */
public class NeonManager extends BaseAPIManager {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final String TAG = getClass().getSimpleName();

    private static final Object mLock = new Object();
    private static NeonManager mInstance;

    private boolean hasPhotosAccess = false;

    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static NeonManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new NeonManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }

    private NeonManager(Context context) {
        mAppContext = context;
    }


    @Override
    public void addPermissionChecks() {
        String[] requiredPermissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        isExternalStorageReadable();
        for (String requiredPermission : requiredPermissions) {
            if (mAppContext.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Neon", requiredPermission + " was PERMISSION_GRANTED");
                this.hasPhotosAccess = true;
            } else {
                Log.i("Neon", requiredPermission + " was PERMISSION_DENIED");
                this.hasPhotosAccess = false;
                break;
            }
        }

    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.i("Neon", "true");
            return true;
        }
        Log.i("Neon", "false");
        return false;
    }


    public ArrayList<JSONObject> getAssets() {
        if (this.hasPhotosAccess) {
            SharedPreferences sharedPreferences = mAppContext.getSharedPreferences(SyncedAssets, Context.MODE_PRIVATE);
            Log.i("Neon", String.format("SyncedAssets size: %d", sharedPreferences.getAll().size()));

            //     sharedPreferences.edit().clear().apply();

            ArrayList<JSONObject> assets = this.getPhotoAssetsImpl(mAppContext);
            assets.addAll(this.getVideoAssetsImpl(mAppContext));

            Log.i("Neon", String.format("assets: %d", assets.size()));

            ArrayList<JSONObject> assetsToSync = null;
            if (isSyncAssetsDelta) {
                assetsToSync = new ArrayList<JSONObject>();
                for (JSONObject asset : assets) {
                    try {
                        String localIdentifier = asset.getJSONObject("PHAsset").getString("localIdentifier");
                        if (!sharedPreferences.contains(localIdentifier)) {
                            // haven't sent this asset in a sync before
                            //        Log.i("Neon", String.format("localIdentifier: %s not found in SyncedAssets", localIdentifier));
                            assetsToSync.add(asset);
                        } else {
                            //        Log.i("Neon", String.format("localIdentifier: %s found in SyncedAssets", localIdentifier));
                        }
                    } catch (JSONException exception) {
                        Log.e("Neon", "assetsToSync, asset failed");
                    }
                }
            } else {
                assetsToSync = new ArrayList<JSONObject>(assets);
            }


            return assets; // return normal assets
        } else {
            Log.i("Neon", "No photos access granted to this app, requires: android.permission.READ_EXTERNAL_STORAGE");
            return null;
        }
    }


    private ArrayList<JSONObject> getPhotoAssetsImpl(Context applicationContext) {
        final String[] projection;
        projection = new String[]{
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.DESCRIPTION,
                MediaStore.Images.ImageColumns.TITLE,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC,
                MediaStore.Images.ImageColumns.PICASA_ID,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
                MediaStore.Images.ImageColumns.HEIGHT,
                MediaStore.Images.ImageColumns.WIDTH,
                MediaStore.Images.ImageColumns.IS_PRIVATE,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Images.Media._ID
        };

        final Cursor cursor = applicationContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");

        ArrayList<JSONObject> assets = new ArrayList<JSONObject>();

        Log.i("Neon", "getPhotoAssetsImpl: found:" + cursor.getCount());

        if (cursor.moveToFirst()) do {
            assets.add(new PhotoAsset(cursor).getMapRepresentation());
        } while (cursor.moveToNext());
        cursor.close();

        return assets;
    }


    private ArrayList<JSONObject> getVideoAssetsImpl(Context applicationContext) {
        final String[] projection;
        projection = new String[]{
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.DESCRIPTION,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.MINI_THUMB_MAGIC,
                MediaStore.Video.VideoColumns.LATITUDE,
                MediaStore.Video.VideoColumns.LONGITUDE,
                MediaStore.Video.VideoColumns.HEIGHT,
                MediaStore.Video.VideoColumns.WIDTH,
                MediaStore.Video.VideoColumns.IS_PRIVATE,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns._ID
        };

        final Cursor cursor = applicationContext.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Video.VideoColumns.DATE_ADDED + " DESC");

        ArrayList<JSONObject> assets = new ArrayList<JSONObject>();

        Log.i("Neon", "getVideoAssetsImpl: found:" + cursor.getCount());

        if (cursor.moveToFirst()) do {
            assets.add(new VideoAsset(cursor).getMapRepresentation());
        } while (cursor.moveToNext());
        cursor.close();

        return assets;
    }



}
