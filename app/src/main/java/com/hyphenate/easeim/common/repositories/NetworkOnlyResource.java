package com.hyphenate.easeim.common.repositories;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.hyphenate.easeim.common.net.ErrorCode;
import com.hyphenate.easeim.common.net.Result;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.utils.DemoLog;


public abstract class NetworkOnlyResource<ResultType> {
    private static final String TAG = "NetworkBoundResource";
    private EaseThreadManager mThreadManager;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    public NetworkOnlyResource() {
        mThreadManager = EaseThreadManager.getInstance();
        if(mThreadManager.isMainThread()) {
            init();
        }else {
            mThreadManager.runOnMainThread(this::init);
        }
    }

    /**
     * work on main thread
     */
    private void init() {
        // 通知UI开始加载
        result.setValue(Resource.loading(null));
        fetchFromNetwork();
    }

    /**
     * work on main thread
     */
    private void fetchFromNetwork() {
        createCall(new ResultCallBack<LiveData<ResultType>>() {
            @Override
            public void onSuccess(LiveData<ResultType> apiResponse) {

                mThreadManager.runOnMainThread(() -> {
                    result.addSource(apiResponse, response-> {
                        result.removeSource(apiResponse);
                        if(response != null) {
                            if(response instanceof Result) {
                                int code = ((Result) response).code;
                                if(code != ErrorCode.EM_NO_ERROR) {
                                    fetchFailed(code, null);
                                }
                            }
                            mThreadManager.runOnIOThread(() -> {
                                try {
                                    saveCallResult(processResponse(response));
                                } catch (Exception e) {
                                    DemoLog.e(TAG, "save call result failed: " + e.toString());
                                }
                                result.postValue(Resource.success(response));
                            });

                        }else {
                            fetchFailed(ErrorCode.EM_ERR_UNKNOWN, null);
                        }
                    });
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                mThreadManager.runOnMainThread(() -> {
                    fetchFailed(error, errorMsg);
                });
            }
        });


    }

    @MainThread
    private void fetchFailed(int code, String message) {
        onFetchFailed();
        result.setValue(Resource.error(code, message,null));
    }

    /**
     * Called to save the result of the API response into the database
     * @param item
     */
    @WorkerThread
    protected void saveCallResult(ResultType item){ }

    /**
     * Process request response
     * @param response
     * @return
     */
    @WorkerThread
    protected ResultType processResponse(ResultType response) {
        return response;
    }

    @MainThread
    protected abstract void createCall(@NonNull ResultCallBack<LiveData<ResultType>> callBack);

    /**
     * Called when the fetch fails. The child class may want to reset components like rate limiter.
     */
    protected void onFetchFailed() {}

    /**
     * Returns a LiveData object that represents the resource that's implemented
     * in the base class.
     * @return
     */
    protected LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

}
