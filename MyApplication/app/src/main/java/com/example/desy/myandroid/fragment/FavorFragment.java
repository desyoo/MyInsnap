package com.example.desy.myandroid.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.desy.myandroid.R;
import com.example.desy.myandroid.adapter.FavoriteAdapter;
import com.example.desy.myandroid.data.FavoriteContract;

public class FavorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FavoriteAdapter.ReloadView{
    // Store instance variables
    private FavoriteAdapter mFavoriteAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int FAVORITE_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FAVORITE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            FavoriteContract.FavoirteEntry.TABLE_NAME + "." + FavoriteContract.FavoirteEntry._ID,
            FavoriteContract.FavoirteEntry.COLUMN_UNIQUE_ID,
            FavoriteContract.FavoirteEntry.COLUMN_TITLE,
            FavoriteContract.FavoirteEntry.COLUMN_DATE,
            FavoriteContract.FavoirteEntry.COLUMN_CATEGORY,
            FavoriteContract.FavoirteEntry.COLUMN_IMAGE,
            FavoriteContract.FavoirteEntry.COLUMN_DESC,
            FavoriteContract.FavoirteEntry.COLUMN_SOURCE,
            FavoriteContract.FavoirteEntry.COLUMN_PRICE,
            FavoriteContract.FavoirteEntry.COLUMN_URL
    };

    // These indices are tied to FAVORITE_COLUMNS.  If FAVORITE_COLUMNS changes, these
    // must change.
    public static final int COL_FAVORITE_ID = 0;
    public static final int COL_FAVORITE_UNIQUE_ID = 1;
    public static final int COL_FAVORITE_TITLE = 2;
    public static final int COL_FAVORITE_DATE = 3;
    public static final int COL_FAVORITE_CATEGORY = 4;
    public static final int COL_FAVORITE_IMAGE = 5;
    public static final int COL_FAVORITE_DESC = 6;
    public static final int COL_FAVORITE_SOURCE = 7;
    public static final int COL_FAVORITE_PRICE = 8;
    public static final int COL_FAVORITE_URL = 9;



    public FavorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavorFragment newInstance(String param1, String param2) {
        FavorFragment fragment = new FavorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mFavoriteAdapter = new FavoriteAdapter(getActivity(), null, 0, this);

        View view = inflater.inflate(R.layout.fragment_favor, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) view.findViewById(R.id.lvFavorite);
        mListView.setAdapter(mFavoriteAdapter);
        // We'll call our MainActivity
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // CursorAdapter returns a cursor at the correct position for getItem(), or null
//                // if it cannot seek to that position.
//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    ((Callback) getActivity())
//                            .onItemSelected(cursor.getString(COL_FAVORITE_URL), cursor.getString(COL_FAVORITE_TITLE));
//                }
//                mPosition = position;
//            }
//        });


        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = FavoriteContract.FavoirteEntry._ID + " DESC";

        Uri favoriteUri = FavoriteContract.FavoirteEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                favoriteUri,
                FAVORITE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoriteAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteAdapter.swapCursor(null);
    }

    @Override
    public void onItemSelected() {
        getLoaderManager().restartLoader(FAVORITE_LOADER, null, this);
    }



}
