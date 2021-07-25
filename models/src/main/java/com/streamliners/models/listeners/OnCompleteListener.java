package com.streamliners.models.listeners;

public interface OnCompleteListener<T> {
    void onCompleted(T t);
    void onFailed(String error);
}
