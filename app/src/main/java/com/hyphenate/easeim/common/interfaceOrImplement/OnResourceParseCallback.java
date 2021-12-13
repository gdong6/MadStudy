package com.hyphenate.easeim.common.interfaceOrImplement;

import androidx.annotation.Nullable;


public abstract class OnResourceParseCallback<T> {
    public boolean hideErrorMsg;

    public OnResourceParseCallback() {}

    public OnResourceParseCallback(boolean hideErrorMsg) {
        this.hideErrorMsg = hideErrorMsg;
    }
    public abstract void onSuccess(@Nullable T data);

    public void onError(int code, String message){}


    public void onLoading(@Nullable T data){}

    public void hideLoading(){}
}
