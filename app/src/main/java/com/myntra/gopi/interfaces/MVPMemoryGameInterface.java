package com.myntra.gopi.interfaces;

/**
 * Created by gopikrishna on 30/11/16.
 */

public interface MVPMemoryGameInterface {

    void showProgress();

    void hideProgress();

    void processNetworkData();

    void loadNextRandomImageInUI(String url, int indexOfElement);

    int getUniqueDefaultResourceIdWithRemoval();
}
