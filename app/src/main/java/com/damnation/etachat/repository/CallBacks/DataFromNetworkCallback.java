package com.damnation.etachat.repository.CallBacks;

import java.util.List;

public interface DataFromNetworkCallback<T> {
    void onSuccess(List<T> list);
    void onError();
}
