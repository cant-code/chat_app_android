package com.damnation.etachat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.damnation.etachat.model.Group;

import java.util.List;

@Dao
public interface GroupDAO {

    @Query("SELECT * FROM `group`")
    List<Group> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Group> groupList);

    @Insert
    void insertOne(Group group);

    @Query("DELETE FROM `group`")
    void deleteAll();
}
