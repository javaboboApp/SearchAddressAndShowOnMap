package com.bigbang.supercoollocation.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bigbang.supercoollocation.model.data.LocationDetails;
import com.bigbang.supercoollocation.model.data.Result;
import com.bigbang.supercoollocation.model.network.LocationService;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.bigbang.supercoollocation.util.Constants.API_KEY;

public class LocationRepositoryImpl implements LocationRepository {


    private final LocationService locationService;
    public static final String TAG = "LocationRepository";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LocationRepositoryImpl(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public LiveData<LocationDetails> getLocationDetails(String address) {
        MutableLiveData<LocationDetails> locationLiveData = new MutableLiveData<>();
        clearDisposables();
        compositeDisposable.add(
                locationService.getLocationDetails(address, API_KEY)
                        .debounce(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(locationDetails -> {
                            Log.d(TAG, "Received - > " + locationDetails.toString());
                            locationLiveData.setValue(locationDetails);
                        }, throwable -> {
                            Log.d(TAG, "An error occured! " + throwable.getStackTrace());
                            throwable.printStackTrace();
                        })
        );
        return locationLiveData;
    }

    @Override
    public void clearDisposables() {
        compositeDisposable.clear();
    }

}
