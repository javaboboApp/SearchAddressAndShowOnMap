package com.bigbang.supercoollocation.model.network;

import com.bigbang.supercoollocation.model.data.LocationDetails;
import com.bigbang.supercoollocation.model.data.Result;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {
    @GET("maps/api/geocode/json")
    public Observable<LocationDetails> getLocationDetails(@Query("address") String address, @Query("key") String apiKey);
}
