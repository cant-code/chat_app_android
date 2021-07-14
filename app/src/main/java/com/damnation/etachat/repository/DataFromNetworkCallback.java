package com.damnation.etachat.repository;

import com.damnation.etachat.http.User;

import java.util.List;

public interface DataFromNetworkCallback<T> {
    void onSuccess(List<T> list);
    void onError();
}
