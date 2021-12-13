package com.hyphenate.easeim.common.interfaceOrImplement;

import android.app.Activity;

import java.util.List;


public interface ActivityState {

    Activity current();

    List<Activity> getActivityList();


    int count();

    boolean isFront();
}
