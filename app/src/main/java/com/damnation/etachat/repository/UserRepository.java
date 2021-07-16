package com.damnation.etachat.repository;

import android.content.Context;
import com.damnation.etachat.database.AppDatabase;
import com.damnation.etachat.database.UserDAO;
import com.damnation.etachat.database.DatabaseProvider;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.model.User;
import com.damnation.etachat.repository.CallBacks.DataFromDatabaseCallback;
import com.damnation.etachat.repository.CallBacks.DataFromNetworkCallback;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {

    private HTTPClient httpClient;
    private Executor executor;
    private AppDatabase database;

    public UserRepository(Context context) {
        httpClient = HTTPClient.INSTANCE;
        database = DatabaseProvider.getInstance(context.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
    }

    public void loadDataFromDatabase(DataFromDatabaseCallback<User> callback) {
        executor.execute(() -> callback.onSuccess(database.userDAO().getAll()));
    }

    public void loadDataFromNetwork(DataFromNetworkCallback<User> callback) {
        executor.execute(() -> {
            List<User> userList = httpClient.loadUsers();
            if (userList == null) {
                callback.onError();
            } else {
                UserDAO userDAO = database.userDAO();
                userDAO.deleteAll();
                userDAO.insertAll(userList);
                callback.onSuccess(userList);
            }
        });
    }
}
