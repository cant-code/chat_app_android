package com.damnation.etachat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.damnation.etachat.model.Group;
import com.damnation.etachat.model.GroupMessages;
import com.damnation.etachat.model.Messages;
import com.damnation.etachat.model.User;

@Database(entities = {User.class, Group.class, Messages.class, GroupMessages.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract GroupDAO groupDAO();
    public abstract MessagesDAO messagesDAO();
    public abstract GroupMessagesDAO groupMessagesDAO();
}
