package com.hyphenate.easeim.common.repositories;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chat.EMPushManager;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.exceptions.HyphenateException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class EMPushManagerRepository extends BaseEMRepository {

    public LiveData<Resource<EMPushConfigs>> getPushConfigsFromServer() {
        return new NetworkBoundResource<EMPushConfigs, EMPushConfigs>() {
            @Override
            protected boolean shouldFetch(EMPushConfigs data) {
                return true;
            }

            @Override
            protected LiveData<EMPushConfigs> loadFromDb() {
                return createLiveData(getPushManager().getPushConfigs());
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<EMPushConfigs>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(()-> {
                    EMPushConfigs configs = null;
                    try {
                        configs = getPushManager().getPushConfigsFromServer();
                        callBack.onSuccess(createLiveData(configs));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }

            @Override
            protected void saveCallResult(EMPushConfigs item) {

            }
        }.asLiveData();
    }


    public EMPushConfigs fetchPushConfigsFromServer() {
        try {
            return getPushManager().getPushConfigsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return null;
    }


    public LiveData<Resource<Boolean>> disableOfflinePush(int start, int end) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        EMClient.getInstance().pushManager().disableOfflinePush(start, end);
                        callBack.onSuccess(createLiveData(true));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> enableOfflinePush() {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(()-> {
                    try {
                        EMClient.getInstance().pushManager().enableOfflinePush();
                        callBack.onSuccess(createLiveData(true));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }
        }.asLiveData();
    }


    public LiveData<Resource<Boolean>> updatePushNickname(String nickname) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getPushManager().asyncUpdatePushNickname(nickname, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> updatePushStyle(EMPushManager.DisplayStyle style) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getPushManager().asyncUpdatePushDisplayStyle(style, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }
}
