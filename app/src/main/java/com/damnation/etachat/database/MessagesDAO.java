package com.damnation.etachat.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.damnation.etachat.model.Messages;

import java.util.List;

@Dao
public interface MessagesDAO {

    @Query("SELECT * FROM messages WHERE (`to` = :toId AND `from` = :fromId) OR (`from` = :toId AND `to` = :fromId)")
    List<Messages> getAll(String toId, String fromId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Messages> messagesList);

    @Insert
    void insertOne(Messages messages);

    @Query("DELETE FROM messages")
    void deleteAll();
}
