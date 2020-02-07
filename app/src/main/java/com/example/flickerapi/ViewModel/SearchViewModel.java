package com.example.flickerapi.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flickerapi.FlickerPhotos;
import com.example.flickerapi.FlickerPhotosSearch;
import com.example.flickerapi.repositories.MyRepository;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<FlickerPhotosSearch> flickerPhotosSearchMutableLiveData;

    private MyRepository myRepository;

    public void init(String text)
    {
        if(flickerPhotosSearchMutableLiveData != null)
            return;

        myRepository = MyRepository.getInstance();
        flickerPhotosSearchMutableLiveData = myRepository.getFlickerPhotosSearch(text);
    }

    public void setFlickerPhotosSearchMutableLiveData(MutableLiveData<FlickerPhotosSearch> flickerPhotosSearchMutableLiveData)
    {
        this.flickerPhotosSearchMutableLiveData = flickerPhotosSearchMutableLiveData;

    }
    public LiveData<FlickerPhotosSearch> getFlickerPhotosSearch()
    {
        return flickerPhotosSearchMutableLiveData;
    }
}
