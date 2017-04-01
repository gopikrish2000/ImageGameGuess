package com.myntra.gopi.apiAdapters;

import com.myntra.gopi.api.GameApi;
import com.myntra.gopi.utils.GameConstants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.myntra.gopi.utils.CommonUtils.isNonNull;
import static com.myntra.gopi.utils.CommonUtils.isNull;

/**
 * Created by gopikrishna on 29/11/16.
 */

public class GameApiAdapter {
    private GameApi gameApiService;
    private static GameApiAdapter instance;

    private GameApiAdapter() {
    }

    public static GameApiAdapter getInstance() {
        if (isNull(instance)) {
            instance = new GameApiAdapter();
        }
        return instance;
    }

    public GameApi getGameApiService() {
        if (isNonNull(gameApiService)) {
            return gameApiService;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(GameConstants.BASE_URL)
                .build();

        gameApiService = retrofit.create(GameApi.class);
        return gameApiService;
    }
}
