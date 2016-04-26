package com.example.desy.myandroid.api;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONException;

public class PhotoAsset extends Asset{
    public PhotoAsset(Cursor cursor) {
        super(cursor);

        try {
            this.phAsset.put("mediaType", 1); //photo
            this.phAsset.put("orientation", cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION)));
//            Log.i("PhotoAsset", "Orientation: " + cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION)));
//            Log.i("PhotoAsset", "Orientation (Media): " + cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)));
        } catch (JSONException e) {
            Log.e("PhotoAsset", e.getMessage());
        }
    }
}