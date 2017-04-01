package com.myntra.gopi.domains;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gopikrishna on 01/12/16.
 */

public class GameResultItem implements Parcelable {

    private int id;
    private String moves;
    private String createdDate;


    public GameResultItem() {
    }

    public GameResultItem(String moves) {
        this.moves = moves;
    }

    public GameResultItem(int id, String moves, String createdDate) {
        this.id = id;
        this.moves = moves;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.moves);
        dest.writeString(this.createdDate);
    }

    protected GameResultItem(Parcel in) {
        this.id = in.readInt();
        this.moves = in.readString();
        this.createdDate = in.readString();
    }

    public static final Parcelable.Creator<GameResultItem> CREATOR = new Parcelable.Creator<GameResultItem>() {
        public GameResultItem createFromParcel(Parcel source) {
            return new GameResultItem(source);
        }

        public GameResultItem[] newArray(int size) {
            return new GameResultItem[size];
        }
    };
}
