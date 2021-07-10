package com.damnation.etachat.repository;

import android.content.Context;
import com.damnation.etachat.database.UserDAO;
import com.damnation.etachat.database.UserDatabase;
import com.damnation.etachat.database.UserDatabaseProvider;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.http.User;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {

    private HTTPClient httpClient;
    private Executor executor;
    private UserDatabase database;

    public UserRepository(Context context) {
        httpClient = HTTPClient.INSTANCE;
        database = UserDatabaseProvider.getInstance(context.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
    }

    public void loadDataFromDatabase(DataFromDatabaseCallback callback) {
        executor.execute(() -> callback.onSuccess(database.userDAO().getAll()));
    }

    public void loadDataFromNetwork(DataFromNetworkCallback callback) {
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
