package com.example.desy.myandroid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.desy.myandroid.R;
import com.example.desy.myandroid.model.Interest;

import java.util.List;

/**
 * Created by desy on 4/21/16.
 */
public class MainProfileAdapter extends RecyclerView.Adapter<MainProfileAdapter.ViewHolder> {
    private Context context;
    private List<Interest> interests;

    public MainProfileAdapter (Context context, List<Interest> interests) {
        this.context = context;
        this.interests = interests;
    }

    @Override
    public int getItemViewType(int position) {
        return 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Interest interest = interests.get(position);
        String interest_name = interest.getInterest();

        if (interest.isChecked()) {
            holder.llBar.setBackgroundColor(context.getResources().getColor(R.color.profile_item));
        } else {
            holder.llBar.setBackgroundColor(context.getResources().getColor(R.color.profile_deselected_color));
            holder.llBar.getBackground().setAlpha(120);
        }

        holder.llBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cbProfile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            holder.llBar.setBackgroundColor(context.getResources().getColor(R.color.profile_deselected_color));
                            holder.llBar.getBackground().setAlpha(120);
                        } else {
                            holder.llBar.setBackgroundColor(context.getResources().getColor(R.color.profile_item));
                        }
                        holder.cbProfile.setChecked(isChecked);
                    }
                });
            }
        });

        holder.cbProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    holder.llBar.setBackgroundColor(context.getResources().getColor(R.color.profile_item));
                } else {
                    holder.llBar.setBackgroundColor(context.getResources().getColor(R.color.profile_deselected_color));
                    holder.llBar.getBackground().setAlpha(120);
                }

                holder.cbProfile.setChecked(((CheckBox) v).isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {
        final View rootView;
        final TextView tvItem;
        final CheckBox cbProfile;
        final LinearLayout llBar;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            this.tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            this.cbProfile = (CheckBox) itemView.findViewById(R.id.cbProfile);
            this.llBar = (LinearLayout) itemView.findViewById(R.id.llBar);
            cbProfile.setChecked(true);
        }
    }

}
