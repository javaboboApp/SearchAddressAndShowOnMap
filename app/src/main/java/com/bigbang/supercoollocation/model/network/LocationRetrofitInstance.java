package com.bigbang.supercoollocation.model.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bigbang.supercoollocation.util.Constants.BASE_URL;

public class LocationRetrofitInstance {

    private static LocationRetrofitInstance locationRetrofitInstance = null;


    public LocationService createLocationService() {
        return getRetrofitInstance().create(LocationService.class);
    }

    public static LocationRetrofitInstance getLocationRetrofitInstance() {
        if (locationRetrofitInstance == null)
            locationRetrofitInstance = new LocationRetrofitInstance();
        return locationRetrofitInstance;
    }

    private Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }



}
