package com.hyphenate.easeim.common.interfaceOrImplement;

import com.hyphenate.EMCallBack;


public abstract class DemoEmCallBack implements EMCallBack {

    @Override
    public void onError(int code, String error) {
        // do something for error
    }

    @Override
    public void onProgress(int progress, String status) {
        // do something in progress
    }
}
