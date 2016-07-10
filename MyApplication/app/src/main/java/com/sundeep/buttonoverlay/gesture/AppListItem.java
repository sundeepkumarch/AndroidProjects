package com.sundeep.buttonoverlay.gesture;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AppListItem implements Parcelable {

    private String appname;
    private String pname;
    private String versionName;
    private int versionCode;
    private Drawable icon;

    public AppListItem() {
    }

    public AppListItem(String appname, String pname, String versionName, int versionCode, Drawable icon) {
        this.appname = appname;
        this.pname = pname;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.icon = icon;
    }

    protected AppListItem(Parcel in) {
        appname = in.readString();
        pname = in.readString();
        versionName = in.readString();
        versionCode = in.readInt();
    }


    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appname);
        dest.writeString(this.pname);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
    }

    public static final Creator<AppListItem> CREATOR = new Creator<AppListItem>() {
        @Override
        public AppListItem createFromParcel(Parcel in) {
            return new AppListItem(in);
        }

        @Override
        public AppListItem[] newArray(int size) {
            return new AppListItem[size];
        }
    };

}
