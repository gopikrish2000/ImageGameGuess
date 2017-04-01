package com.myntra.gopi;

import com.myntra.gopi.utils.CommonUtils;

/**
 * Created by gopikrishna on 29/11/16.
 */
public class GameConfig {

    private int numberOfElements = 9;
    private boolean doFlipAnimationOnClick = true;
    private int delayTimeInSeconds = 15;
    private int networkCallTimeout = 10;
    private static GameConfig ourInstance;


    public static GameConfig getInstance() {
        if (CommonUtils.isNonNull(ourInstance)) {
            return ourInstance;
        }
        ourInstance = new GameConfig();
        return ourInstance;
    }

    private GameConfig() {
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isDoFlipAnimationOnClick() {
        return doFlipAnimationOnClick;
    }

    public int getDelayTimeInSeconds() {
        return delayTimeInSeconds;
    }

    public int getNetworkCallTimeout() {
        return networkCallTimeout;
    }
}
