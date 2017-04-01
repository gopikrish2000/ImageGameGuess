package com.myntra.gopi.domains;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.myntra.gopi.utils.CommonUtils;

/**
 * Created by gopikrishna on 27/11/16.
 */

public class GameItem implements Parcelable {
    private int position;
    @SerializedName("m")
    private String url;
    private String state;
    private int drawableResourceId = -1;

    public GameItem() {
    }

    public GameItem(int position, String url, String state) {
        this.position = position;
        this.url = url;
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getdrawableResourceId() {
        return drawableResourceId;
    }

    public void setdrawableResourceId(int drawableResourceId) {
        this.drawableResourceId = drawableResourceId;
    }

    public boolean isDrawableNull() {
        return drawableResourceId <= 0;
    }

    public String getUrl() {
        return url;
    }

    public String getBigImageUrl() {
        if (CommonUtils.isNullOrEmpty(url)) {
            return "";
        }
        return url.replace("_m.", "_z.");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "GameItem{" +
                "position=" + position +
                ", url='" + url + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeString(this.url);
        dest.writeString(this.state);
    }

    protected GameItem(Parcel in) {
        this.position = in.readInt();
        this.url = in.readString();
        this.state = in.readString();
    }

    public static final Parcelable.Creator<GameItem> CREATOR = new Parcelable.Creator<GameItem>() {
        public GameItem createFromParcel(Parcel source) {
            return new GameItem(source);
        }

        public GameItem[] newArray(int size) {
            return new GameItem[size];
        }
    };
}
