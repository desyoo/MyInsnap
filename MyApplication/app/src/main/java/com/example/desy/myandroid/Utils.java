package com.example.desy.myandroid;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by desy on 4/19/16.
 */
public class Utils {
    //public static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss.SSSZZ");
    private static final String TAG = Utils.class.getSimpleName();

    public static class Constants {
        public static final String DEVICEID = "device_id";

        public final static String CRITERIA = "criteria";
        public final static String MAIN_IMAGE_ID = "main_image_id";
        public final static String POSITION = "position";
        public final static String IMAGE_IDS_LIST = "image_id_list";
        public final static String VIDEO_URI = "video_uri";
        public final static String REALTIME_RESPONSE = "realtime";


        public final class SharedPreferences {
            public final static String REJECTED_INTERESTS = "rejectedInterests";
            public final static String REALTIME_PROFILE_CACHE = "profileCache";

            public final class ProfileCache {
                public final static String SAVE_RESULT_PROFILE = "resultProfile";
                public final static String IS_PROFILE_CACHED = "savedResultProfile";
            }
        }

        public static class MediaMetaData {
            public final static String LOCATION = "location";
            public final static String DATE_OF_CREATION = "localCreationDate";
            public final static String MEDIA_TYPE = "mediaType";
        }

        public static class AssetFeatureKey {
            public final static String PHOTO = "IsPhoto";
            public final static String VIDEO = "IsVideo";
            public final static String LANDSCAPE = "IsLandscape";
            public final static String PORTRIAT = "IsPortrait";
            public final static String SELFIE = "IsSelfie";
            public final static String NON_SELFIE = "IsNotSelfie";
            public final static String SCREENSHOT = "IsScreenshot";
            public final static String ALLTIME = "AllTime";
            public final static String DOWNLOAD = "IsDownload";
            public final static String SQUARE = "IsSquare";
            public final static String AM = "IsAM";
            public final static String PM = "IsPM";
            public final static String WEEKEND = "IsWeekend";
            public final static String WEEKDAY = "IsWeekday";
        }

        public static class MediaType {
            public static final int IMAGE = 1;
            public static final int VIDEO = 2;
        }
    }


//    public static class Date {
//        public static String getCurrentUTCTimestamp() {
//            return DateTime.now().toString(Utils.dtf);
//        }
//    }


    public static boolean isNetworkAvailable (Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }



    public static HashMap<String, String> getThumbnailPathForLocalFile(Context context, int mediaType) {
        Cursor thumbCursor = null;
        HashMap<String, String> list = new HashMap<>();
        try {
            Uri uri = null;
            String selection = null;
            String dataColumnName = null;
            String mediaIdColumnName = null;
            switch (mediaType) {
                case Constants.MediaType.IMAGE:
                    dataColumnName = MediaStore.MediaColumns.DATA;
                    mediaIdColumnName = MediaStore.Images.Thumbnails.IMAGE_ID;
                    uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
                    selection = MediaStore.Images.Thumbnails.KIND + " = "
                            + MediaStore.Images.Thumbnails.MINI_KIND;
                    break;
                case Constants.MediaType.VIDEO:
                    dataColumnName = MediaStore.MediaColumns.DATA;
                    mediaIdColumnName = MediaStore.Video.Thumbnails.VIDEO_ID;
                    uri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
                    selection = MediaStore.Video.Thumbnails.KIND + " = "
                            + MediaStore.Video.Thumbnails.MINI_KIND;
                    break;
            }

            String[] projection = {dataColumnName, mediaIdColumnName};
            thumbCursor = context.getContentResolver().
                    query(uri
                            , projection
                            , selection, null, null);

            if (thumbCursor != null && thumbCursor.moveToFirst()) {
                // the path is stored in the DATA column
                int dataIndex = thumbCursor.getColumnIndexOrThrow(dataColumnName);
                int idIndex = thumbCursor.getColumnIndexOrThrow(mediaIdColumnName);

                do {
                    String thumbnailPath = thumbCursor.getString(dataIndex);
                    String id = thumbCursor.getString(idIndex);
                    //Log.i(TAG, id + " : " + thumbnailPath);
                    list.put(id, thumbnailPath);
                } while (thumbCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (thumbCursor != null) {
                thumbCursor.close();
            }
        }

        return list;
    }



    public static Bitmap resizeBitMap(String filePath, int targetWidth,
                                      int targetHeight, int orientation) {
        Bitmap bitMapImage = null;
        // First, get the dimensions of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        double sampleSize = 0;
        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
                .abs(options.outWidth - targetWidth);

        if (options.outHeight * options.outWidth * 2 >= 1638) {
            // Load, scaling to smallest power of 2 that'll get it <= desired
            // dimensions
            sampleSize = scaleByHeight ? options.outHeight / targetHeight
                    : options.outWidth / targetWidth;
            sampleSize = (int) Math.pow(2d,
                    Math.floor(Math.log(sampleSize) / Math.log(2d)));
        }


        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[128];
        while (true) {
            try {
                options.inSampleSize = (int) sampleSize;
                bitMapImage = BitmapFactory.decodeFile(filePath, options);

                if (options.outWidth > options.outHeight ) {
                    if (orientation != 0) {
                        Matrix matrix = new Matrix();
                        switch (orientation) {
                            case 90:
                                matrix.postRotate(90);
                                bitMapImage = Bitmap.createBitmap(bitMapImage, 0, 0, bitMapImage.getWidth(), bitMapImage.getHeight(), matrix, true);
                                break;
                            case 180:
                                matrix.postRotate(180);
                                bitMapImage = Bitmap.createBitmap(bitMapImage, 0, 0, bitMapImage.getWidth(), bitMapImage.getHeight(), matrix, true);
                                break;
                            default:
                                bitMapImage = Bitmap.createBitmap(bitMapImage, 0, 0, bitMapImage.getWidth(), bitMapImage.getHeight(), matrix, true);
                                break;
                        }
                    }
                }

                break;
            } catch (Exception ex) {
                try {
                    sampleSize = sampleSize * 2;
                } catch (Exception ex1) {

                }
            }
        }

        return bitMapImage;
    }


}
