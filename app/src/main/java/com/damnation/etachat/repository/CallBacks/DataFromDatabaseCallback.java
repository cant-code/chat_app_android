package com.damnation.etachat.repository.CallBacks;

import java.util.List;

public interface DataFromDatabaseCallback<T> {
    void onSuccess(List<T> list);
}
