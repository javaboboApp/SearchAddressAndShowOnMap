package com.bigbang.supercoollocation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bigbang.supercoollocation.repository.LocationRepositoryImpl;

public class LocationViewModelFactory implements ViewModelProvider.Factory {

    private final LocationRepositoryImpl locationRepositoryImpl;

    public LocationViewModelFactory(LocationRepositoryImpl locationRepositoryImpl) {
        this.locationRepositoryImpl = locationRepositoryImpl;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LocationViewModel(locationRepositoryImpl);
    }
}
