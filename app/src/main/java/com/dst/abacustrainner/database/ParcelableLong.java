package com.dst.abacustrainner.database;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableLong implements Parcelable {
    private long value;

    public ParcelableLong(long value) {
        this.value = value;
    }

    protected ParcelableLong(Parcel in) {
        value = in.readLong();
    }

    public static final Creator<ParcelableLong> CREATOR = new Creator<ParcelableLong>() {
        @Override
        public ParcelableLong createFromParcel(Parcel in) {
            return new ParcelableLong(in);
        }

        @Override
        public ParcelableLong[] newArray(int size) {
            return new ParcelableLong[size];
        }
    };

    public long getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(value);
    }
}