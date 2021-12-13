package com.hyphenate.easeim.common.interfaceOrImplement;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.login.activity.SplashActivity;

import java.util.ArrayList;
import java.util.List;



public class UserActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks, ActivityState {
    private List<Activity> activityList=new ArrayList<>();
    private List<Activity> resumeActivity=new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.e("ActivityLifecycle", "onActivityCreated "+activity.getLocalClassName());
        activityList.add(0, activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("ActivityLifecycle", "onActivityStarted "+activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e("ActivityLifecycle", "onActivityResumed activity's taskId = "+activity.getTaskId() + " name: "+activity.getLocalClassName());
        if (!resumeActivity.contains(activity)) {
            resumeActivity.add(activity);
            if(resumeActivity.size() == 1) {
                //do nothing
            }
            restartSingleInstanceActivity(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("ActivityLifecycle", "onActivityPaused "+activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("ActivityLifecycle", "onActivityStopped "+activity.getLocalClassName());
        resumeActivity.remove(activity);
        if(resumeActivity.isEmpty()) {
            Activity a = getOtherTaskSingleInstanceActivity(activity.getTaskId());
            if(isTargetSingleInstance(a)) {
                makeTaskToFront(a);
            }
            Log.e("ActivityLifecycle", "在后台了");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Log.e("ActivityLifecycle", "onActivitySaveInstanceState "+activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("ActivityLifecycle", "onActivityDestroyed "+activity.getLocalClassName());
        activityList.remove(activity);
    }

    @Override
    public Activity current() {
        return activityList.size()>0 ? activityList.get(0):null;
    }

    @Override
    public List<Activity> getActivityList() {
        return activityList;
    }

    @Override
    public int count() {
        return activityList.size();
    }

    @Override
    public boolean isFront() {
        return resumeActivity.size() > 0;
    }


    public void skipToTarget(Class<?> cls) {
        if(activityList != null && activityList.size() > 0) {
            current().startActivity(new Intent(current(), cls));
            for (Activity activity : activityList) {
                activity.finish();
            }
        }

    }


    public void finishTarget(Class<?> cls) {
        if(activityList != null && !activityList.isEmpty()) {
            for (Activity activity : activityList) {
                if(activity.getClass() == cls) {
                    activity.finish();
                }
            }
        }
    }


    public boolean isOnForeground() {
        return resumeActivity != null && !resumeActivity.isEmpty();
    }



    private void restartSingleInstanceActivity(Activity activity) {
        boolean isClickByFloat = activity.getIntent().getBooleanExtra("isClickByFloat", false);
        if(isClickByFloat) {
            return;
        }

        if(resumeActivity.size() == 1 && resumeActivity.get(0) instanceof SplashActivity) {
            return;
        }

        if(resumeActivity.size() >= 1 && activityList.size() > 1) {
            Activity a = getOtherTaskSingleInstanceActivity(resumeActivity.get(0).getTaskId());
            if(a != null && !a.isFinishing()
                    && a != activity
                    && a.getTaskId() != activity.getTaskId()
            ){
                Log.e("ActivityLifecycle", "启动了activity = "+a.getClass().getName());
                activity.startActivity(new Intent(activity, a.getClass()));
            }
        }
    }

    private Activity getOtherTaskSingleInstanceActivity(int taskId) {
        if(taskId != 0 && activityList.size() > 1) {
            for (Activity activity : activityList) {
                if(activity.getTaskId() != taskId) {
                    if(isTargetSingleInstance(activity)) {
                        return activity;
                    }
                }
            }
        }
        return null;
    }


    public void makeMainTaskToFront(Activity activity) {
        if(activity.isFinishing() && resumeActivity.size() == 1 && resumeActivity.get(0) == activity && activityList.size() > 1) {
            ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(20);
            for(int i = 0; i < runningTasks.size(); i++) {
                ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(i);
                ComponentName topActivity = taskInfo.topActivity;
                if(topActivity != null && topActivity.getPackageName().equals(activity.getPackageName())) {
                    int taskId;

                        taskId = taskInfo.id;

                    //将任务栈置于前台
                    Log.e("ActivityLifecycle", "执行moveTaskToFront，current activity:"+activity.getClass().getName());
                    manager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }
        }
    }
    
    private boolean isTargetSingleInstance(Activity activity) {
        if(activity == null) {
            return false;
        }
        CharSequence title = activity.getTitle();
        if(TextUtils.equals(title, activity.getString(R.string.demo_activity_label_video_call))
                || TextUtils.equals(title, activity.getString(R.string.demo_activity_label_multi_call))) {
            return true;
        }
        return false;
    }
    
    private void makeTaskToFront(Activity activity) {
        Log.e("ActivityLifecycle", "makeTaskToFront activity: "+activity.getLocalClassName());
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        manager.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
    }
}
