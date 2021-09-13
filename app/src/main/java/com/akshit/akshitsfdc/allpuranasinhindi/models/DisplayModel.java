package com.akshit.akshitsfdc.allpuranasinhindi.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DisplayModel implements Parcelable {

    private String displayHeader;
    private String displayKey;
    private int limit;

    public DisplayModel() {
    }

    protected DisplayModel(Parcel in) {
        displayHeader = in.readString();
        displayKey = in.readString();
        limit = in.readInt();
    }

    public static final Creator<DisplayModel> CREATOR = new Creator<DisplayModel>() {
        @Override
        public DisplayModel createFromParcel(Parcel in) {
            return new DisplayModel(in);
        }

        @Override
        public DisplayModel[] newArray(int size) {
            return new DisplayModel[size];
        }
    };

    public String getDisplayHeader() {
        return displayHeader;
    }

    public void setDisplayHeader(String displayHeader) {
        this.displayHeader = displayHeader;
    }

    public String getDisplayKey() {
        return displayKey;
    }

    public void setDisplayKey(String displayKey) {
        this.displayKey = displayKey;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(displayHeader);
        parcel.writeString(displayKey);
        parcel.writeInt(limit);
    }
}
