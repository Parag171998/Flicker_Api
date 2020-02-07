package com.example.flickerapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickerApi {

    // API 1-   https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&per_page=20&page=1&api_key=6f102c62f41998d151e5a1b48713cf13&format=json&nojsoncallback=1&extras=url_s;

    String baseUrl = "https://api.flickr.com/services/rest/";
    String searchUrl = "https://api.flickr.com/services/";

    @GET("/services/rest/?method=flickr.photos.getRecent&per_page=10&page=2&api_key=6f102c62f41998d151e5a1b48713cf13&format=json&nojsoncallback=1&extras=url_s")
    Call<FlickerPhotos> getFlickerPhotosPage2();

    @GET("/services/rest/?method=flickr.photos.getRecent&per_page=10&page=3&api_key=6f102c62f41998d151e5a1b48713cf13&format=json&nojsoncallback=1&extras=url_s")
    Call<FlickerPhotos> getFlickerPhotosPage3();

    @GET("rest/?method=flickr.photos.search&per_page=25&api_key=6f102c62f41998d151e5a1b48713cf13&format=json&nojsoncallback=1&extras=url_s")
    Call<FlickerPhotosSearch> getFlickerSearchPhotos(@Query("text") String text);
}
