package com.damnation.etachat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.damnation.etachat.http.Group;

import java.util.List;

@Dao
public interface GroupDAO {

    @Query("SELECT * FROM `group`")
    List<Group> getAll();

    @Insert
    void insertAll(List<Group> groupList);

    @Query("DELETE FROM `group`")
    void deleteAll();
}
