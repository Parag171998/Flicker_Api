package com.example.flickerapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    LayoutInflater layoutInflater;
    List<Photo> photoList;

    public SearchAdapter(Context context, List<Photo> photoList) {
        this.layoutInflater = layoutInflater.from(context);
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.row_post_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {

        Glide.with(layoutInflater.getContext()).load(photoList.get(position).getUrlS()).into(holder.flickPhoto);
        holder.title.setText(photoList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView flickPhoto;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            flickPhoto = itemView.findViewById(R.id.row_post_img);
            title = itemView.findViewById(R.id.row_post_title);
        }
    }

}
