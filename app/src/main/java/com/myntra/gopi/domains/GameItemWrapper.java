package com.myntra.gopi.domains;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gopikrishna on 29/11/16.
 */

public class GameItemWrapper {

    @SerializedName("items")
    private List<GameMediaWrapper> gameItemList;

    public GameItemWrapper() {
    }

    public List<GameMediaWrapper> getGameItemList() {
        return gameItemList;
    }

    public void setGameItemList(List<GameMediaWrapper> gameItemList) {
        this.gameItemList = gameItemList;
    }
}
