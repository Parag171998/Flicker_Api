package com.example.flickerapi.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Mydao {

    @Insert
    public void addPhotoRoom(PhotoRoom photoRoom);

    @Query("select * from PhotoRoom")
    public List<PhotoRoom> getPhotoRoom();

    @Query("DELETE FROM PhotoRoom ")
    public void deleteAll();
}
