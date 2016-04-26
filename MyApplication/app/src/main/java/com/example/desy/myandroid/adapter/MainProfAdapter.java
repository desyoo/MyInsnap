package com.example.desy.myandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.desy.myandroid.R;
import com.example.desy.myandroid.flowlayout.FlowLayout;
import com.example.desy.myandroid.flowlayout.TagAdapter;
import com.example.desy.myandroid.flowlayout.TagFlowLayout;
import com.example.desy.myandroid.model.Interest;

import java.util.List;

/**
 * Created by desy on 4/22/16.
 */
public class MainProfAdapter extends TagAdapter<Interest> {
    private List<Interest> interests;
    private Context context;
    private TagFlowLayout tagFlowLayout;


    private static class ViewHolder {
        TextView name;
    }


    public MainProfAdapter(Context context, TagFlowLayout TF, List<Interest> datas) {
        super(datas);
        this.context = context;
        interests = datas;
        tagFlowLayout = TF;
    }

    @Override
    public View getView(FlowLayout parent, int position, Interest interest) {
        final LayoutInflater mInflater = LayoutInflater.from(context);
        TextView tv = (TextView) mInflater.inflate(R.layout.tv, tagFlowLayout, false);
        tv.setText(interest.getInterest());
        return tv;
    }
}
