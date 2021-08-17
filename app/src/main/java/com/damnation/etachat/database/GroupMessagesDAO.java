package com.damnation.etachat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.damnation.etachat.model.GroupMessages;

import java.util.List;

@Dao
public interface GroupMessagesDAO {

    @Query("SELECT * FROM GroupMessages WHERE `group` = :groupId")
    List<GroupMessages> getAll(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GroupMessages> messagesList);

    @Insert
    void insertOne(GroupMessages messages);

    @Query("DELETE FROM groupmessages")
    void deleteAll();
}
