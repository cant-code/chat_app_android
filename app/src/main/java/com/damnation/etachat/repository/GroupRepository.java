package com.damnation.etachat.repository;

import android.content.Context;
import com.damnation.etachat.database.AppDatabase;
import com.damnation.etachat.database.DatabaseProvider;
import com.damnation.etachat.database.GroupDAO;
import com.damnation.etachat.http.Group;
import com.damnation.etachat.http.HTTPClient;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GroupRepository {

    private HTTPClient httpClient;
    private Executor executor;
    private AppDatabase database;

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
}
