package com.damnation.etachat.repository;

import android.content.Context;
import com.damnation.etachat.database.AppDatabase;
import com.damnation.etachat.database.DatabaseProvider;
import com.damnation.etachat.database.GroupDAO;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.model.Group;
import com.damnation.etachat.repository.CallBacks.AddToDBCallback;
import com.damnation.etachat.repository.CallBacks.DataFromDatabaseCallback;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GroupRepository {

    private final HTTPClient httpClient;
    private final Executor executor;
    private final AppDatabase database;

    public GroupRepository(Context context) {
        httpClient = HTTPClient.INSTANCE;
        database = DatabaseProvider.getInstance(context.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
    }

    public void loadDataFromDatabase(DataFromDatabaseCallback<Group> callback) {
        executor.execute(() -> callback.onSuccess(database.groupDAO().getAll()));
    }

    public void loadDataFromNetwork(DataFromNetworkCallback<Group> callback) {
        executor.execute(() -> {
            List<Group> groupList = httpClient.loadGroup();
            if (groupList == null) {
                callback.onError();
            } else {
                GroupDAO groupDAO = database.groupDAO();
                groupDAO.deleteAll();
                groupDAO.insertAll(groupList);
                callback.onSuccess(groupList);
            }
        });
    }

    public void addOrJoinRoom(AddToDBCallback<Group> callback, String name, String type) {
        executor.execute(() -> {
            Group group = httpClient.addOrJoinGroup(name, type);
            if(group == null) {
                callback.onError();
            } else {
                GroupDAO groupDAO = database.groupDAO();
                groupDAO.insertOne(group);
                callback.onSuccess(group);
            }
        });
    }
}
