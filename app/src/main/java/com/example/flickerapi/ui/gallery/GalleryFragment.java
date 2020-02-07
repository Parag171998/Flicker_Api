package com.example.flickerapi.ui.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flickerapi.FlickHome;
import com.example.flickerapi.FlickerPhotosSearch;
import com.example.flickerapi.Photo;
import com.example.flickerapi.R;
import com.example.flickerapi.SearchAdapter;
import com.example.flickerapi.ViewModel.SearchViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private SearchViewModel searchViewModel;

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<Photo> photoList = new ArrayList<>();
    private EditText searchView;
    private Button searchBtn;
    private Observable<String> stringObservable;
    private io.reactivex.Observer<String> stringObserver;
    private ConstraintLayout constraintLayout;
    private Snackbar snackbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        constraintLayout = root.findViewById(R.id.myConstrain);
        recyclerView = root.findViewById(R.id.searchRecyclerview);

        searchView = root.findViewById(R.id.searchText);
        searchBtn = root.findViewById(R.id.searchBtn);

        initRecyclerView();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = searchView.getText().toString();

                if(!text.contentEquals("")) {
                    stringObservable = Observable.just(searchView.getText().toString());

                    stringObservable.subscribe(stringObserver);
                }
            }
        });

       stringObserver = new io.reactivex.Observer<String>() {
           @Override
           public void onSubscribe(Disposable d) {

           }

           @Override
           public void onNext(String s) {

               photoList.clear();

               searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);

               searchViewModel.setFlickerPhotosSearchMutableLiveData(null);

               searchViewModel.init(s);

               searchViewModel.getFlickerPhotosSearch().observe(getActivity(), new Observer<FlickerPhotosSearch>() {
                   @Override
                   public void onChanged(FlickerPhotosSearch flickerPhotosSearch) {

                       if(flickerPhotosSearch != null) {

                           if(flickerPhotosSearch.getPhotos() != null) {

                               if(flickerPhotosSearch.getPhotos().getPhoto() != null) {
                                   List<Photo> newPhotoList = flickerPhotosSearch.getPhotos().getPhoto();
                                   photoList.addAll(newPhotoList);

                                   searchAdapter.notifyDataSetChanged();
                               }
                           }
                       }
                   }
               });
           }

           @Override
           public void onError(Throwable e) {

           }

           @Override
           public void onComplete() {

           }
       };


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

    private void initRecyclerView() {

        if(searchAdapter == null) {
            searchAdapter = new SearchAdapter(getActivity(), photoList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(searchAdapter);
        }
        else
            searchAdapter.notifyDataSetChanged();
    }


    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getExtras()!=null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                    if(snackbar != null)
                    {
                        snackbar = Snackbar.make(constraintLayout,"You are online",Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                else {
                    showSnakeBar();
                }
            }
        }
    };

    private void showSnakeBar() {
                snackbar = Snackbar.make(constraintLayout,"You are offline",Snackbar.LENGTH_INDEFINITE)

                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar snackbar1;

                        if(isNetworkConnected())
                        {
                            snackbar1 = Snackbar.make(constraintLayout,"Yor are online",Snackbar.LENGTH_LONG);
                            snackbar1.show();
                        }
                        else
                        {
                            snackbar1 = Snackbar.make(constraintLayout,"Yor are still offline",Snackbar.LENGTH_LONG);
                            snackbar1.show();
                        }

                    }
                });
        snackbar.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


}