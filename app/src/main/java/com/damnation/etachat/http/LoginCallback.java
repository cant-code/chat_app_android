package com.damnation.etachat.http;

import java.util.HashMap;

public interface LoginCallback {
    void onSuccess(HashMap<String, String> map);
    void onError(String message);
}
