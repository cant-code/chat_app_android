package com.damnation.etachat.repository;

import com.damnation.etachat.http.User;

import java.util.List;

public interface DataFromDatabaseCallback {
    void onSuccess(List<User> list);
}