package com.example.flickerapi.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.flickerapi.FlickHome;
import com.example.flickerapi.FlickerApi;
import com.example.flickerapi.FlickerPhotos;
import com.example.flickerapi.Photo;
import com.example.flickerapi.PhotoAdapter;
import com.example.flickerapi.R;
import com.example.flickerapi.room.MyappDatabse;
import com.example.flickerapi.room.PhotoRoom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static MyappDatabse myappDatabse;

    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    LinearLayoutManager linearLayoutManager;
    Boolean isScrolling = false;
    int currentItem, totalItem, scrolloutItem;
    Call<FlickerPhotos> flickerPhotosCall;
    List<Photo> photoList;
    ProgressBar progressBar;

    Boolean isOnline = false;

    List<PhotoRoom> photoRoomList;

    FlickerApi flickerApi;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        myappDatabse = Room.databaseBuilder(getActivity(), MyappDatabse.class, "photodb").allowMainThreadQueries().build();

        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        photoRoomList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = root.findViewById(R.id.recyclerview);

        progressBar = root.findViewById(R.id.progressbar);

        recyclerView.setLayoutManager(linearLayoutManager);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(FlickerApi.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        flickerApi = retrofit.create(FlickerApi.class);

        flickerPhotosCall = flickerApi.getFlickerPhotosPage2();

        photoList = new ArrayList<>();

        flickerPhotosCall.enqueue(new Callback<FlickerPhotos>() {
            @Override
            public void onResponse(Call<FlickerPhotos> call, Response<FlickerPhotos> response) {

                FlickerPhotos flickerPhotos = response.body();

                photoList.addAll(flickerPhotos.getPhotos().getPhoto());

                myappDatabse.mydao().deleteAll();

                photoAdapter = new PhotoAdapter(getContext(), photoList);

                recyclerView.setAdapter(photoAdapter);

                if (photoList != null) {
                    UrlToByte urlToByte = new UrlToByte();

                    urlToByte.execute();
                }

            }

            @Override
            public void onFailure(Call<FlickerPhotos> call, Throwable t) {

                photoRoomList = myappDatabse.mydao().getPhotoRoom();

                photoAdapter = new PhotoAdapter(photoRoomList, getContext());

                recyclerView.setAdapter(photoAdapter);

                isOnline = true;

                Toast.makeText(getActivity(),"You are offline", Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }


            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItem = linearLayoutManager.getChildCount();
                totalItem = linearLayoutManager.getItemCount();
                scrolloutItem = linearLayoutManager.findLastVisibleItemPosition();

                if (isScrolling && (currentItem + scrolloutItem) == totalItem + 2 && isNetworkConnected()) {
                    isScrolling = false;
                    fetchData();
                }
            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void fetchData() {

        progressBar.setVisibility(View.VISIBLE);

        flickerPhotosCall = flickerApi.getFlickerPhotosPage3();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flickerPhotosCall.clone().enqueue(new Callback<FlickerPhotos>() {
                    @Override
                    public void onResponse(Call<FlickerPhotos> call, Response<FlickerPhotos> response) {

                            FlickerPhotos flickerPhotos = response.body();
                            photoList.addAll(flickerPhotos.getPhotos().getPhoto());
                            photoAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onFailure(Call<FlickerPhotos> call, Throwable t) {

                    }
                });
            }
        }, 3000);

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private class UrlToByte extends AsyncTask<String, byte[], byte[]> {

        @Override
        protected byte[] doInBackground(String... strings) {

            int id = 0;

            for (Photo photo : photoList) {
                PhotoRoom photoRoom = new PhotoRoom(id, photo.getTitle(), getBytes(photo.getUrlS()));
                id++;
                myappDatabse.mydao().addPhotoRoom(photoRoom);
            }
            return null;
        }

        private byte[] getBytes(String imageUrl) {

            URL url = null;
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try (InputStream stream = url.openStream()) {
                byte[] buffer = new byte[4096];

                while (true) {
                    int bytesRead = stream.read(buffer);
                    if (bytesRead < 0) {
                        break;
                    }
                    output.write(buffer, 0, bytesRead);
                }
                publishProgress(output.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output.toByteArray();
        }

    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getExtras()!=null && isOnline) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                    Intent i = new Intent(getActivity(),FlickHome.class);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        }
    };
}
