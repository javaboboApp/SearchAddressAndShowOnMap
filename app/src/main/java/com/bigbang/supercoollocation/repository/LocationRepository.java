package com.bigbang.supercoollocation.repository;

import androidx.lifecycle.LiveData;

import com.bigbang.supercoollocation.model.data.LocationDetails;
import com.bigbang.supercoollocation.model.data.Result;

public interface LocationRepository {
    LiveData<LocationDetails> getLocationDetails(String latLng);

    void clearDisposables();
}
