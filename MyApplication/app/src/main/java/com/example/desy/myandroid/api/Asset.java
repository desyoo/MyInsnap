package com.example.desy.myandroid.api;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by desy on 4/19/16.
 */
public abstract class Asset {
    protected JSONObject phAsset;
    public Asset(Cursor cursor) {
        this.phAsset = new JSONObject();

        try {
            this.phAsset.put("platform", "Android");
            this.phAsset.put("localIdentifier", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)));
            this.phAsset.put("metadataType", 1); // light version of the object

            //Log.i("Asset:localIdentifier", this.phAsset.getString("localIdentifier"));

            //   long lModificationDate = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
            //   this.PHAsset.put("localModificationDate", new DateTime( lModificationDate , DateTimeZone.getDefault() ).toString());

            //long lCreationDate = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
            //DateTime dt = new DateTime(lCreationDate * 1000);
            //this.phAsset.put("localCreationDate", dt.toString(Utils.dtf));
            //Log.d("Asset",this.phAsset.get("localCreationDate").toString());
            //    this.phAsset.put("joda", dt.toString(dtf));

            // FIELDS NOT POSSIBLE
            // isFavorite
            // HDR

            this.phAsset.put("pixelHeight", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)));
            this.phAsset.put("pixelWidth", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)));

            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE));

            if(latitude == 0 && longitude == 0) {
                this.phAsset.put("location", "100,200");
            }
            else {
                this.phAsset.put("location", latitude + "," + longitude);
            }

            this.phAsset.put("bucketDisplayName", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));

//            Log.i("Neon:Asset", "BUCKET_ID:" + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID)));
//            Log.i("Neon:Asset", "BUCKET_DISPLAY_NAME:" + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
//            Log.i("Neon:Asset", "DESCRIPTION:" + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DESCRIPTION)));

        } catch (JSONException exception) {
            Log.e("Neon:Asset", exception.getMessage());
        }
    }

    public JSONObject getMapRepresentation() {
        JSONObject ph = new JSONObject();
        try {
            ph.put("PHAsset", this.phAsset);
        } catch (JSONException exception) {
            Log.e("Neon:Asset", exception.getMessage());
        }
        return ph;
    }

    @Override
    public String toString() {
        return this.phAsset.toString();
    }
}
