package com.myntra.gopi.domains;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gopikrishna on 29/11/16.
 */

public class GameMediaWrapper {

    @SerializedName("media")
    private GameItem gameItem;

    public GameItem getGameItem() {
        return gameItem;
    }

    public void setGameItem(GameItem gameItem) {
        this.gameItem = gameItem;
    }
}
