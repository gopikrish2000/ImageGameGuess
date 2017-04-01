package com.myntra.gopi.api;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by gopikrishna on 29/11/16.
 */

public interface GameApi {

    @GET("/services/feeds/photos_public.gne")
    Observable<ResponseBody> getRandomGameImages(@QueryMap HashMap<String, String> queryMap);
}
