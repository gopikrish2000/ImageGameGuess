package com.myntra.gopi.presenters;

import android.app.Activity;
import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.myntra.gopi.GameConfig;
import com.myntra.gopi.apiAdapters.GameApiAdapter;
import com.myntra.gopi.domains.GameItem;
import com.myntra.gopi.domains.GameItemWrapper;
import com.myntra.gopi.domains.GameMediaWrapper;
import com.myntra.gopi.intdefs.GameStates;
import com.myntra.gopi.interfaces.BasePresenterInterface;
import com.myntra.gopi.interfaces.MVPMemoryGameInterface;
import com.myntra.gopi.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.myntra.gopi.utils.RxApiUtil.build;

/**
 * Created by gopikrishna on 30/11/16.
 */

public class MemoryGamePresenter implements BasePresenterInterface {

    private final String TAG = MemoryGamePresenter.class.getSimpleName();
    private final ArrayList<GameItem> gameItemList;
    private final Set<Integer> nonGuessedValueIndices;
    private final Activity context;
    private MVPMemoryGameInterface mvpMemoryGameInterface;
    private CompositeSubscription lifeCycle, networkCallSubscription;
    private boolean isSubscribed;
    private boolean isResultCame;

    public MemoryGamePresenter(Activity context, MVPMemoryGameInterface mvpMemoryGameInterface, ArrayList<GameItem> gameItemList, Set<Integer> nonGuessedValueIndices) {
        this.context = context;
        this.mvpMemoryGameInterface = mvpMemoryGameInterface;
        lifeCycle = new CompositeSubscription();
        networkCallSubscription = new CompositeSubscription();
        this.gameItemList = gameItemList;
        this.nonGuessedValueIndices = nonGuessedValueIndices;
        this.isSubscribed = true;
        this.isResultCame = false;
    }

    public void doLogicForFreshLaunch() {
        for (int index = 0; index < GameConfig.getInstance().getNumberOfElements(); index++) {
            nonGuessedValueIndices.add(index);
        }
        mvpMemoryGameInterface.showProgress();
        HashMap<String, String> networkParamMap = new HashMap<>();
        networkParamMap.put("format", "json");

        // Doing below big processing in background thread because of 2 reasons
        // One is getting only JsonP response not Json and Second i didn't find any limit parameter in api ( so that i get only 9 urls in response)
        // Process is getInputStream -> convertToString -> convertJsonPstringToJsonString -> convertJsonToObject -> getListOfItems
        // -> Trim list to GameConfig.getInstance().getNumberOfElements() -> prepareProperObjects from it.
        networkCallSubscription.add(build(GameApiAdapter.getInstance().getGameApiService().getRandomGameImages(networkParamMap)
                .map(CommonUtils::convertResponseToString)
                .map(CommonUtils::convertJsonPToJson)
                .map(json -> new Gson().fromJson(json, GameItemWrapper.class))
                .map(GameItemWrapper::getGameItemList)
                .map(gameMediaWrapperList -> {
                    int numberOfElements = GameConfig.getInstance().getNumberOfElements();
                    return gameMediaWrapperList.size() > numberOfElements ? gameMediaWrapperList.subList(0, numberOfElements) : gameMediaWrapperList;
                })
                .switchMap(this::fillGameItemsWithPosition))
                .subscribe(itemList -> {
                    isResultCame = true;
                    mvpMemoryGameInterface.hideProgress();
                    mvpMemoryGameInterface.processNetworkData();
                }, error -> {
                    isResultCame = true;
                    mvpMemoryGameInterface.hideProgress();
                    // Offline/Network call failed Game Flow
                    networkCallSubscription.add(build(offlineGameFillItemsWithPosition()).subscribe(s -> {
                        mvpMemoryGameInterface.processNetworkData();
                    }));
                }));

        // to handle the time out which is currently set to 10 seconds.
        lifeCycle.add(build(Observable.just(true).delay(GameConfig.getInstance().getNetworkCallTimeout(), TimeUnit.SECONDS)).subscribe(s -> {
            if (!isResultCame) {
                CommonUtils.doUnsubscribe(networkCallSubscription);
                mvpMemoryGameInterface.hideProgress();
                // Offline/Network call failed Game Flow
                build(offlineGameFillItemsWithPosition()).subscribe(bool -> {
                    mvpMemoryGameInterface.processNetworkData();
                });
            }
        }));
    }

    private Observable<List<GameItem>> fillGameItemsWithPosition(List<GameMediaWrapper> itemList) {
        return Observable.create(subscriber -> {
            for (int i = 0; i < itemList.size(); i++) {
                GameItem gameItem = itemList.get(i).getGameItem();
                gameItem.setPosition(i);
                gameItem.setState(GameStates.NORMAL);
                if (CommonUtils.isNullOrEmpty(gameItem.getBigImageUrl())) { // bigImageUrl has better quality than normal image.
                    int uniqueDefaultResourceId = mvpMemoryGameInterface.getUniqueDefaultResourceIdWithRemoval();
                    gameItem.setdrawableResourceId(uniqueDefaultResourceId);
                }
                gameItemList.add(gameItem);
            }
            subscriber.onNext(gameItemList);
        });
    }

    private Observable<Boolean> offlineGameFillItemsWithPosition() {
        List<Integer> positionList = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        return Observable.create(subscriber -> {
            for (int i = 0; i < 9; i++) {
                GameItem gameItem = new GameItem();
                gameItem.setPosition(i);
                gameItem.setState(GameStates.NORMAL);
                int randomNumberInRange = CommonUtils.getRandomNumberInRange(0, positionList.size() - 1);
                int randomIndex = positionList.remove(randomNumberInRange);
                int defaultImageResourceId = CommonUtils.getDefaultImageResourceId(randomIndex);
                gameItem.setdrawableResourceId(defaultImageResourceId); // Always preference is giving to gameItem.resourceId then to gameItem.url
                gameItemList.add(gameItem);
            }
            subscriber.onNext(true);
        });
    }

    public void generateNextRandomImage() {
        if (nonGuessedValueIndices.isEmpty()) {
            return;
        }
        lifeCycle.add(build(Observable.<Pair<String, Integer>>create(subscriber -> {
            // concept is put all values in list -> generate random index -> remove that element that is ur random element.
            int randomNumber = CommonUtils.getRandomNumberInRange(0, nonGuessedValueIndices.size() - 1);
            int elementAtIndex = getElementAtIndex(nonGuessedValueIndices, randomNumber);
            String url = gameItemList.get(elementAtIndex).getBigImageUrl();
            subscriber.onNext(Pair.create(url, elementAtIndex));
        })).subscribe(pair -> {
            mvpMemoryGameInterface.loadNextRandomImageInUI(pair.first, pair.second);
        }));
    }


    private int getElementAtIndex(Set<Integer> nonGuessedValueIndices, int randomNumber) {
        int i = 0;  // As set doesn't has getItemAtItem implementing it.
        for (Integer index : nonGuessedValueIndices) {
            if (i == randomNumber) {
                return index;
            }
            i++;
        }
        throw new IndexOutOfBoundsException("index " + randomNumber + " not found in GameList");
    }

    @Override
    public void unSubscribe() {
        isSubscribed = false;
        CommonUtils.doUnsubscribe(lifeCycle);
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }
}
