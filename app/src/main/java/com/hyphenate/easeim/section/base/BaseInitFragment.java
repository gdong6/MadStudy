package com.hyphenate.easeim.section.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseInitFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initArgument();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initViewModel();
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    protected abstract int getLayoutId();


    protected void initArgument() {}


    protected void initView(Bundle savedInstanceState) {
        Log.e("TAG", "fragment = "+this.getClass().getSimpleName());
    }



    protected void initViewModel() {}


    protected void initListener() {}


    protected void initData() {}


    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }
}
