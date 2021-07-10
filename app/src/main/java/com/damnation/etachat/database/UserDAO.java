package com.damnation.etachat.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.damnation.etachat.http.User;

import java.util.List;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Insert
    void insertAll(List<User> userList);

    @Query("DELETE FROM user")
    void deleteAll();
}
