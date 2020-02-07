package com.example.flickerapi.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PhotoRoom {

    @PrimaryKey
    private int id;

    @ColumnInfo
    private String title;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] imageByte;


    public PhotoRoom(int id, String title, byte[] imageByte) {
        this.id = id;
        this.title = title;
        this.imageByte = imageByte;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
