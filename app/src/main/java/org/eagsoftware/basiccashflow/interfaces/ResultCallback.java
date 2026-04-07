package org.eagsoftware.basiccashflow.interfaces;

@SuppressWarnings({"EmptyMethod", "unused"})
public interface ResultCallback {
    void onSuccess();
    void onError(Exception exc);
}
