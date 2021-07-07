package com.damnation.etachat.http;

public interface RegisterCallback {
    void onSuccess();
    void onError(String message);
}
