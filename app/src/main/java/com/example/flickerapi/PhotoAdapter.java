package com.example.flickerapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flickerapi.room.PhotoRoom;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {


    LayoutInflater layoutInflater;
    List<Photo>photoList;
    List<PhotoRoom> photoRoomList;

    public PhotoAdapter(List<PhotoRoom> photoRoomList, Context context ) {
        this.layoutInflater = layoutInflater.from(context);
        this.photoRoomList = photoRoomList;
    }

    public PhotoAdapter(Context context, List<Photo> photoList) {
        this.layoutInflater = layoutInflater.from(context);
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.row_post_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(photoList != null) {
            Glide.with(layoutInflater.getContext()).load(photoList.get(position).getUrlS()).into(holder.flickPhoto);
            holder.title.setText(photoList.get(position).getTitle());

        }
        else
        {
            int length = photoRoomList.get(position).getImageByte().length;

            Bitmap bitmap = BitmapFactory.decodeByteArray(photoRoomList.get(position).getImageByte() , 0,length);

            holder.flickPhoto.setImageBitmap(bitmap);
            holder.title.setText(photoRoomList.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {

        if(photoList != null)
            return photoList.size();
        else
            return photoRoomList.size();
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
