package com.example.flickerapi.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PhotoRoom.class},version = 1)
public abstract class MyappDatabse extends RoomDatabase {

    public abstract Mydao mydao();

}
