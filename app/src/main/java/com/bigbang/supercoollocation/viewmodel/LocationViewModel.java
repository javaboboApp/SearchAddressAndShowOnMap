package com.bigbang.supercoollocation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bigbang.supercoollocation.model.data.LocationDetails;
import com.bigbang.supercoollocation.model.data.Result;
import com.bigbang.supercoollocation.repository.LocationRepository;

public class LocationViewModel extends ViewModel {


    private final LocationRepository locationRepositoryImpl;

    public LocationViewModel(LocationRepository locationRepository) {
        this.locationRepositoryImpl = locationRepository;
    }

    public LiveData<LocationDetails>  getLocationDetails(String latLng){
        return locationRepositoryImpl.getLocationDetails(latLng);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        locationRepositoryImpl.clearDisposables();
    }
}
