package com.damnation.etachat.database;

import android.content.Context;
import androidx.room.Room;

public class UserDatabaseProvider {

    private static volatile UserDatabase instance;

    public static UserDatabase getInstance(Context context) {
        if(instance == null) {
            synchronized (UserDatabaseProvider.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, UserDatabase.class, "user-database")
                            .build();
                }
            }
        }
        return instance;
    }
}
