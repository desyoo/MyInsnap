package com.example.desy.myandroid.api;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONException;

/**
 * Created by desy on 4/20/16.
 */
public class VideoAsset extends Asset {
    public VideoAsset(Cursor cursor) {
        super(cursor);

        try {
            this.phAsset.put("mediaType", 2); //video
            this.phAsset.put("videoDuration", cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)));
        } catch (JSONException e) {
            Log.e("PhotoAsset", e.getMessage());
        }

    }
}
