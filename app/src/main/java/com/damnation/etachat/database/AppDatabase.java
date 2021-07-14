package com.damnation.etachat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.damnation.etachat.http.Group;
import com.damnation.etachat.http.User;

@Database(entities = {User.class, Group.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract GroupDAO groupDAO();
}
