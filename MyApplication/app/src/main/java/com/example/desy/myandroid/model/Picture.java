package com.example.desy.myandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by desy on 4/23/16.
 */
public class Picture implements Parcelable {
    private  String mId;
    private   int length;
    private int mMediaType;
    private int orientation;

    public Picture(String id, int mMediaType, int length, int orientation) {
        this.mId = id;
        this.mMediaType = mMediaType;
        this.length = length;
        this.orientation = orientation;
    }

    public String getmId() {
        return mId;
    }

    public int getLength() {
        return length;
    }

    public int getmMediaType() {
        return mMediaType;
    }

    public int getOrientation() {
        return orientation;
    }

    protected Picture(Parcel in) {
        mId = in.readString();
        length = in.readInt();
        mMediaType = in.readInt();
        orientation = in.readInt();
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeInt(length);
        dest.writeInt(mMediaType);
        dest.writeInt(orientation);
    }


}
