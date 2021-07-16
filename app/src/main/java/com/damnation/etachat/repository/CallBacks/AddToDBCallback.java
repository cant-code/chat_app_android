package com.damnation.etachat.repository.CallBacks;

public interface AddToDBCallback<T> {
    void onSuccess(T data);
    void onError();
}
