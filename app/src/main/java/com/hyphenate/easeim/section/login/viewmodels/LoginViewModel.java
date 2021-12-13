package com.hyphenate.easeim.section.login.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.livedatas.SingleSourceLiveData;
import com.hyphenate.easeim.common.repositories.EMClientRepository;


public class LoginViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private SingleSourceLiveData<Resource<String>> registerObservable;
    private SingleSourceLiveData<Integer> pageObservable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        registerObservable = new SingleSourceLiveData<>();
        pageObservable = new SingleSourceLiveData<>();
    }


    public LiveData<Integer> getPageSelect() {
        return pageObservable;
    }


    public void setPageSelect(int page) {
        pageObservable.setValue(page);
    }


    public void register(String userName, String pwd) {
        registerObservable.setSource(mRepository.registerToHx(userName, pwd));
    }

    public LiveData<Resource<String>> getRegisterObservable() {
        return registerObservable;
    }

    public void clearRegisterInfo() {
        registerObservable.setValue(null);
    }

}
