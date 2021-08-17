package com.damnation.etachat.repository;

import android.content.Context;
import com.damnation.etachat.database.AppDatabase;
import com.damnation.etachat.database.DatabaseProvider;
import com.damnation.etachat.database.GroupMessagesDAO;
import com.damnation.etachat.database.MessagesDAO;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.model.GroupMessages;
import com.damnation.etachat.model.Messages;
import com.damnation.etachat.repository.CallBacks.DataFromDatabaseCallback;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessagesRepository {

    private HTTPClient httpClient;
    private Executor executor;
    private AppDatabase database;

    public MessagesRepository(Context context) {
        httpClient = HTTPClient.INSTANCE;
        database = DatabaseProvider.getInstance(context.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
    }

    public void loadGlobalDataFromDatabase(DataFromDatabaseCallback<Messages> callback) {
        executor.execute(() -> callback.onSuccess(database.messagesDAO().getGlobal()));
    }

    public void loadGlobalDataFromNetwork(DataFromNetworkCallback<Messages> callback) {
        executor.execute(() -> {
            List<Messages> messagesList = httpClient.getGlobalMessages();
            if(messagesList == null) {
                callback.onError();
            } else {
                MessagesDAO messagesDAO = database.messagesDAO();
                messagesDAO.insertAll(messagesList);
                callback.onSuccess(messagesList);
            }
        });
    }

    public void loadDataFromDatabase(DataFromDatabaseCallback<Messages> callback, String id, String from) {
        executor.execute(() -> callback.onSuccess(database.messagesDAO().getAll(id, from)));
    }

    public void loadDataFromNetwork(DataFromNetworkCallback<Messages> callback, String id) {
        executor.execute(() -> {
            List<Messages> messagesList = httpClient.loadMessages(id);
            if (messagesList == null) {
                callback.onError();
            } else {
                MessagesDAO messagesDAO = database.messagesDAO();
                messagesDAO.insertAll(messagesList);
                callback.onSuccess(messagesList);
            }
        });
    }

    public void loadGroupDataFromDatabase(DataFromDatabaseCallback<GroupMessages> callback, String groupId) {
        executor.execute(() -> callback.onSuccess(database.groupMessagesDAO().getAll(groupId)));
    }

    public void loadGroupDataFromNetwork(DataFromNetworkCallback<GroupMessages> callback, String groupId) {
        executor.execute(() -> {
            List<GroupMessages> messagesList = httpClient.getGroupMessages(groupId);
            if(messagesList == null) {
                callback.onError();
            } else {
                GroupMessagesDAO groupMessagesDAO = database.groupMessagesDAO();
                groupMessagesDAO.insertAll(messagesList);
                callback.onSuccess(messagesList);
            }
        });
    }
}
