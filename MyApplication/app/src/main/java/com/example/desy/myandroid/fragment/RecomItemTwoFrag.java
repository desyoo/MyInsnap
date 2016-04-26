package com.example.desy.myandroid.fragment;

import android.os.Bundle;

import com.example.desy.myandroid.R;
import com.example.desy.myandroid.model.Picture;

public class RecomItemTwoFrag extends BaseRecomItemFrag {


    public RecomItemTwoFrag() {
        // Required empty public constructor
    }

    public static RecomItemTwoFrag newInstance(Picture picture) {
        RecomItemTwoFrag fragment = new RecomItemTwoFrag();
        Bundle args = new Bundle();
        args.putParcelable("mpicture", picture);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    int getFragmentId() {
        return R.layout.fragment_recom_item;
    }

}
