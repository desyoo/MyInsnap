package com.example.desy.myandroid.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.example.desy.myandroid.R;
import com.example.desy.myandroid.Utils;
import com.example.desy.myandroid.data.FavoriteDbHelper;
import com.example.desy.myandroid.model.Picture;
import com.example.desy.myandroid.data.FavoriteContract.FavoirteEntry;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by desy on 4/23/16.
 */
public abstract class BaseRecomItemFrag extends Fragment{
    protected static final String TAG = BaseRecomItemFrag.class.getSimpleName();
    protected Context mContext;
    protected HashMap<String, String> mBitmapPath;
    protected Bitmap mPlaceHolderBitmap;
    protected Picture picture;

    public BaseRecomItemFrag() {
        // Required empty public constructor
    }

    abstract int getFragmentId();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            picture = getArguments().getParcelable("mpicture");
        }
        mContext = getContext();
        mBitmapPath = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(getFragmentId(), container, false);
        // Inflate the layout for this fragment
        ImageView imageView = (ImageView) rootView.findViewById(R.id.ivImage);

        loadBitmap(picture, imageView);

        final CheckBox btnLike = (CheckBox) rootView.findViewById(R.id.cbLike);

        btnLike.setOnCheckedChangeListener(null);

        if (checkValueInsideTableExsist()) {
            btnLike.setChecked(true);
        } else {
            btnLike.setChecked(false);
        }

        btnLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The like was posted successfully, change the button to reflect the change
                //recommendation.setIsLiked(isChecked);
                //RecommendationManager.getInstance(getContext()).savedRecommendation(RecommendationAction.LIKED);
                if (isChecked) {
                    storeInDB();
                } else {
                    //getAllContacts();    //for testing
                    removeItemInDB();
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


    public void loadBitmap(Picture holder, final ImageView imageView) {
        if (cancelPotentialWork(holder.getmId(), imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(holder);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<Picture, Void, Bitmap> {
        private final String TAG = BitmapWorkerTask.class.getSimpleName();
        private final WeakReference<ImageView> imageViewReference;
        private String data = "NULL";//id

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Picture... params) {
            Picture mHolder = params[0];
            String id = mHolder.getmId();
            int mediaType = mHolder.getmMediaType();
            Log.d(TAG, "worker thread started for :" + id);
            if (mBitmapPath.isEmpty()) {
                mBitmapPath = Utils.getThumbnailPathForLocalFile(mContext, Utils.Constants.MediaType.IMAGE);
                mBitmapPath.putAll(Utils.getThumbnailPathForLocalFile(mContext, Utils.Constants.MediaType.VIDEO));
            }
            data = id;

            int length = mHolder.getLength();
            long startTS = SystemClock.uptimeMillis();
            Bitmap bitmap = null;
            String thumbnailPath = mBitmapPath.get(id);

            if (thumbnailPath != null) {
                bitmap = Utils.resizeBitMap(thumbnailPath, length / 4, length / 4, mHolder.getOrientation());
            }
            if (bitmap == null) {
                Log.e(TAG, "Bitmap is NULL");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 4;
                options.inJustDecodeBounds = false;

                try {
                    switch (mediaType) {//This will getThumbnail and if not present then it will create one.
                        case Utils.Constants.MediaType.IMAGE:
                            bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),
                                    Integer.parseInt(id),
                                    MediaStore.Images.Thumbnails.MINI_KIND,
                                    options);
                            break;
                        case Utils.Constants.MediaType.VIDEO:
                            bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(),
                                    Integer.parseInt(id),
                                    MediaStore.Video.Thumbnails.MINI_KIND,
                                    options);
                            break;
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            Log.d("Time", "worker thread stopped for :" + id + ", Time to get image : " + (SystemClock.uptimeMillis() - startTS));
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }else{
                Log.e(TAG, "Bitmap is NULL");
            }
        }
    }


    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData.equalsIgnoreCase("NULL") || !bitmapData.equalsIgnoreCase(data)) {
                // Cancel previous task
                Log.v(TAG, "cancelling previous task!!...");
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }


    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }


    public boolean checkValueInsideTableExsist(){
        //String uniqueID = makeUniqueID();
        FavoriteDbHelper mDbHelper = new FavoriteDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selectQuery = "SELECT " + FavoirteEntry._ID + " FROM " + FavoirteEntry.TABLE_NAME + " WHERE " + FavoirteEntry.COLUMN_UNIQUE_ID + " = '" + picture.getmId() +"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() == 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public void storeInDB() {
        FavoriteDbHelper mDbHelper = new FavoriteDbHelper(getContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //String uniqueID = makeUniqueID();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FavoirteEntry.COLUMN_UNIQUE_ID, picture.getmId());
        values.put(FavoirteEntry.COLUMN_TITLE, "your photo");
        values.put(FavoirteEntry.COLUMN_CATEGORY, "my_test");
        values.put(FavoirteEntry.COLUMN_DESC, "trying to show people");
        values.put(FavoirteEntry.COLUMN_IMAGE, picture.getmId());
        values.put(FavoirteEntry.COLUMN_PRICE, "free");
        values.put(FavoirteEntry.COLUMN_SOURCE, picture.getmMediaType());
        values.put(FavoirteEntry.COLUMN_URL, picture.getmId());
        values.put(FavoirteEntry.COLUMN_DATE, "today"); // if we need time

        // Insert the new row, returning the primary key value of the new row
        db.insert(FavoirteEntry.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public void removeItemInDB() {
        FavoriteDbHelper mDbHelper = new FavoriteDbHelper(getContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //String uniqueID = makeUniqueID();

        //String selectQuery = "SELECT * FROM " + FavoirteEntry.TABLE_NAME + " ORDER BY " + FavoirteEntry._ID + " DESC LIMIT 1";
        String selectQuery = "SELECT " + FavoirteEntry._ID + " FROM " + FavoirteEntry.TABLE_NAME + " WHERE " + FavoirteEntry.COLUMN_UNIQUE_ID + " = '" + picture.getmId() +"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            db.delete(FavoirteEntry.TABLE_NAME, FavoirteEntry._ID + "=" + cursor.getString(0), null);
        }
        db.close(); // Closing database connection
    }
}
