package com.example.flickerapi.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.flickerapi.FlickerApi;
import com.example.flickerapi.FlickerPhotosSearch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRepository {

    private static MyRepository myRepositoryInstance;
    public static MyRepository getInstance()
    {
        if(myRepositoryInstance == null)
            myRepositoryInstance = new MyRepository();

        return myRepositoryInstance;
    }
    
    public MutableLiveData<FlickerPhotosSearch> getFlickerPhotosSearch(String text)
    {
        final MutableLiveData<FlickerPhotosSearch> newdata = new MutableLiveData<>();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(FlickerApi.searchUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FlickerApi flickerApi = retrofit.create(FlickerApi.class);

        final Call<FlickerPhotosSearch> flickerPhotosSearchCall = flickerApi.getFlickerSearchPhotos(text);

        flickerPhotosSearchCall.enqueue(new Callback<FlickerPhotosSearch>() {
            @Override
            public void onResponse(Call<FlickerPhotosSearch> call, Response<FlickerPhotosSearch> response) {
                if(response.isSuccessful())
                    newdata.setValue(response.body());
            }

            @Override
            public void onFailure(Call<FlickerPhotosSearch> call, Throwable t) {

                t.printStackTrace();
            }
        });

          return newdata;

    }

}
