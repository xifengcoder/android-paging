package com.example.android.codelabs.paging.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ResultBean implements Serializable, Parcelable {
    public String name;
    public String password;

    public ResultBean() {
    }


    public ResultBean(Parcel in) {
        name = in.readString();
        password = in.readString();
    }

    public static final Creator<ResultBean> CREATOR = new Creator<ResultBean>() {
        @Override
        public ResultBean createFromParcel(Parcel in) {
            return new ResultBean(in);
        }

        @Override
        public ResultBean[] newArray(int size) {
            return new ResultBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
