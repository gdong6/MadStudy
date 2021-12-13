package com.hyphenate.easeim.common.repositories;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.easeim.common.net.ErrorCode;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.net.Result;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.utils.DemoLog;


public abstract class NetworkBoundResource<ResultType, RequestType> {
    private static final String TAG = "NetworkBoundResource";
    private EaseThreadManager mThreadManager;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private LiveData<ResultType> lastFailSource;

    public NetworkBoundResource() {
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
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = safeLoadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if(shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            }else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    /**
     * work on main thread
     * @param dbSource
     */
    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        result.addSource(dbSource, newData-> setValue(Resource.loading(newData)));
        createCall(new ResultCallBack<LiveData<RequestType>>() {
            @Override
            public void onSuccess(LiveData<RequestType> apiResponse) {
                mThreadManager.runOnMainThread(() -> {
                    result.addSource(apiResponse, response-> {
                        result.removeSource(apiResponse);
                        result.removeSource(dbSource);
                        if(response != null) {
                            if(response instanceof Result) {
                                int code = ((Result) response).code;
                                if(code != ErrorCode.EM_NO_ERROR) {
                                    fetchFailed(code, dbSource, null);
                                }
                            }
                            mThreadManager.runOnIOThread(() -> {
                                try {
                                    saveCallResult(processResponse(response));
                                } catch (Exception e) {
                                    DemoLog.e(TAG, "save call result failed: " + e.toString());
                                }
                                mThreadManager.runOnMainThread(() ->
                                        result.addSource(safeLoadFromDb(), newData -> {
                                            setValue(Resource.success(newData));
                                        }));
                            });

                        }else {
                            fetchFailed(ErrorCode.EM_ERR_UNKNOWN, dbSource, null);
                        }
                    });
                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                mThreadManager.runOnMainThread(() -> {
                    fetchFailed(error, dbSource, errorMsg);
                });
            }
        });


    }

    private LiveData<ResultType> safeLoadFromDb() {
        LiveData<ResultType> dbSource;
        try {
            dbSource = loadFromDb();
        } catch (Exception e) {
            DemoLog.e(TAG, "safe load from db failed: " + e.toString());
            dbSource = new MutableLiveData<>(null);
        }
        return dbSource;
    }

    @MainThread
    private void fetchFailed(int code, LiveData<ResultType> dbSource, String message) {
        onFetchFailed();
        try {
            result.addSource(dbSource, newData -> setValue(Resource.error(code, message, newData)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if(result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }


    @WorkerThread
    protected RequestType processResponse(RequestType response) {
        return response;
    }


    @MainThread
    protected abstract boolean shouldFetch(ResultType data);

    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();


    @MainThread
    protected abstract void createCall(ResultCallBack<LiveData<RequestType>> callBack);

    /**
     * Called to save the result of the API response into the database
     * @param item
     */
    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

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
