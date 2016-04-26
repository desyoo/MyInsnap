package com.example.desy.myandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.desy.myandroid.R;
import com.example.desy.myandroid.adapter.MainProfAdapter;
import com.example.desy.myandroid.flowlayout.TagFlowLayout;
import com.example.desy.myandroid.model.Interest;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    List<Interest> interests;
    private String[] mVals = new String[]
            {"Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                    "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                    "Android", "Weclome Hello", "Button Text", "TextView", "This is so awesome"};

    private TagFlowLayout mFlowLayout;


    //private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//        interests = Interest.createContactsList(20);
//
//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
//        final MainProfileAdapter mainProfileAdapter = new MainProfileAdapter(getContext(), interests);
//        recyclerView.setAdapter(mainProfileAdapter);
//        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 4);
//        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return mainProfileAdapter.getItemViewType(position);
//            }
//        });
//        recyclerView.setLayoutManager(mGridLayoutManager);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final LayoutInflater mInflater = LayoutInflater.from(getActivity());
        mFlowLayout = (TagFlowLayout) view.findViewById(R.id.id_flowlayout);
        createProfile();
        MainProfAdapter mainProfAdapter = new MainProfAdapter(getContext(), mFlowLayout, interests);
        mFlowLayout.setAdapter(mainProfAdapter);

//        mFlowLayout.setAdapter(new TagAdapter<String>(mVals)
//        {
//
//            @Override
//            public View getView(FlowLayout parent, int position, String s)
//            {
//                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
//                        mFlowLayout, false);
//                tv.setText(s);
//                return tv;
//            }
//        });
    }

    public void createProfile() {
        interests = new ArrayList<>();
        interests.add(new Interest("sports"));
        interests.add(new Interest("food"));
        interests.add(new Interest("beauty"));
        interests.add(new Interest("school"));
        interests.add(new Interest("study"));
        interests.add(new Interest("woman"));
        interests.add(new Interest("man"));
        interests.add(new Interest("game"));
        interests.add(new Interest("book"));
        interests.add(new Interest("animal"));
        interests.add(new Interest("nature"));
        interests.add(new Interest("science"));
        interests.add(new Interest("earth"));
        interests.add(new Interest("ocean"));
        interests.add(new Interest("heaven"));
        interests.add(new Interest("god"));
        interests.add(new Interest("relationship"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
