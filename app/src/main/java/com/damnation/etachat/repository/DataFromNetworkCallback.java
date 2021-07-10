package com.damnation.etachat.repository;

import com.damnation.etachat.http.User;

import java.util.List;

public interface DataFromNetworkCallback {
    void onSuccess(List<User> userList);
    void onError();
}
