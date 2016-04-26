package com.example.desy.myandroid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.example.desy.myandroid.R;
import com.example.desy.myandroid.data.FavoriteContract.FavoirteEntry;
import com.example.desy.myandroid.data.FavoriteDbHelper;
import com.example.desy.myandroid.fragment.FavorFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by desy on 11/5/15.
 */
public class FavoriteAdapter extends CursorAdapter {
    private String TAG = FavoriteAdapter.class.getSimpleName();
    private Context mContext;
    private ReloadView mReloadViewListener;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String webView, String title);
    }



    public interface ReloadView {
        public void onItemSelected();
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView titleView;
        public final TextView descriptionView;
        public final TextView categoryView;
        public final ImageButton iconButton;
        public final SwipeLayout swipeLayout;

        public ViewHolder(View view) {
            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);
            iconView = (ImageView) view.findViewById(R.id.ivFavoritePic);
            titleView = (TextView) view.findViewById(R.id.tvFavoriteTitle);
            descriptionView = (TextView) view.findViewById(R.id.tvFavoriteDesc);
            categoryView = (TextView) view.findViewById(R.id.tvFavoriteCategory);
            iconButton = (ImageButton) view.findViewById(R.id.ibDeleteFavorite);
        }
    }



    public FavoriteAdapter(Context context, Cursor c, int flags, Fragment fragment) {
        super(context, c, flags);
        mContext = context;
        if(fragment instanceof ReloadView) {
            mReloadViewListener = (ReloadView) fragment;
        }
        else {
            Log.i(TAG, String.format("doesn't support"));
        }
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());

        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        final int position = cursor.getPosition();

        Picasso.with(mContext).load(cursor.getString(FavorFragment.COL_FAVORITE_IMAGE)).error(R.drawable.no_picture_available).fit().centerCrop().into(viewHolder.iconView);

        String title = cursor.getString(FavorFragment.COL_FAVORITE_TITLE);
        viewHolder.titleView.setText(title);

        String description = cursor.getString(FavorFragment.COL_FAVORITE_DESC);
        viewHolder.descriptionView.setText(description);

        String category = cursor.getString(FavorFragment.COL_FAVORITE_CATEGORY);
        viewHolder.categoryView.setText(category);

        // For accessibility, add a content description to the icon field
        viewHolder.iconView.setContentDescription(description);

        viewHolder.iconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteDbHelper mDbHelper = new FavoriteDbHelper(mContext);
                // Gets the data repository in write mode
                cursor.moveToPosition(position);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(FavoirteEntry.TABLE_NAME, FavoirteEntry.COLUMN_UNIQUE_ID + " = " + "'" + cursor.getString(FavorFragment.COL_FAVORITE_UNIQUE_ID) + "'", null);
                db.close();
                mReloadViewListener.onItemSelected();
            }
        });

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);
                ((Callback) mContext)
                        .onItemSelected(cursor.getString(FavorFragment.COL_FAVORITE_URL),
                                cursor.getString(FavorFragment.COL_FAVORITE_TITLE));
            }
        });

    }


}
