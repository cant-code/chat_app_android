package com.damnation.etachat.repository;

import android.content.Context;
import com.damnation.etachat.database.AppDatabase;
import com.damnation.etachat.database.DatabaseProvider;
import com.damnation.etachat.database.MessagesDAO;
import com.damnation.etachat.http.HTTPClient;
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
}
