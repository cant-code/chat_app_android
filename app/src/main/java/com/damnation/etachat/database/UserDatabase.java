package com.damnation.etachat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.damnation.etachat.http.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
}
