package com.hyphenate.easeim.section.me.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.NotificationReceiver;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.common.widget.ArrowItemView;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;
import com.hyphenate.chat.EMUserInfo.*;

import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class UserDetailActivity extends BaseInitActivity {
    static private String TAG =  "UserDetailActivity";
    private EaseTitleBar titleBar;
    private ArrowItemView itemNickname;
    private EaseImageView headImageView;
    private String headImageUrl = null;
    private String nickName;
    private String selectHeadUrl = "";
    private EditText urlInput;
    private Button save;
    private NotificationManagerCompat notificationManager;
    public static final String CHANNEL_1_ID = "channel1";

    public static void actionStart(Context context,String nickName,String url) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.putExtra("imageUrl",url);
        intent.putExtra("nickName",nickName);
        context.startActivity(intent);
    }

    /**
     * init intent
     * @param intent
     */
    @Override
    protected void initIntent(Intent intent){
        if(intent != null){
            headImageUrl = intent.getStringExtra("imageUrl");
            nickName = intent.getStringExtra("nickName");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_user_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemNickname = findViewById(R.id.item_nickname);
        headImageView = findViewById(R.id.tv_headImage_view);
        urlInput = findViewById(R.id.urlInput);
        save = findViewById(R.id.save);
        notificationManager = NotificationManagerCompat.from(this);
    }


    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
        itemNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OfflinePushNickActivity.class);
                intent.putExtra("nickName",nickName);
                startActivityForResult(intent, 2);
            }
        });
        
        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selectHeadUrl = urlInput.getText().toString();

                //selectHeadUrl = "https://cdn1.vectorstock.com/i/1000x1000/21/00/cute-little-boy-head-comic-character-vector-29032100.jpg";



                EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfoType.AVATAR_URL, selectHeadUrl, new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        EMLog.d(TAG, "updateOwnInfoByAttribute :" + value);
                        showToast(R.string.demo_head_image_update_success);
                        PreferenceManager.getInstance().setCurrentUserAvatar(selectHeadUrl);
                        DemoHelper.getInstance().getUserProfileManager().updateUserAvatar(selectHeadUrl);
                        EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                        //发送联系人更新事件
                        event.message = selectHeadUrl;
                        LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
                        getIntent().putExtra("headImage", selectHeadUrl);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        EMLog.d(TAG, "updateOwnInfoByAttribute  error:" + error + " errorMsg:" + errorMsg);
                        showToast(R.string.demo_head_image_update_failed);
                    }
                });
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOnChannel1(v);
            }
        });

    }


    @Override
    protected void initData() {
        super.initData();
        if(headImageUrl != null && headImageUrl.length()> 0){
            Glide.with(mContext).load(headImageUrl).placeholder(R.drawable.em_login_logo).into(headImageView);
        }
        if(headImageUrl == null || nickName == null){
            intSelfDate();
        }

        //增加数据变化监听
        addLiveDataObserver();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == 1 && resultCode == RESULT_OK)) {
            if(data != null) {
                headImageUrl = data.getStringExtra("headImage");
                Glide.with(mContext).load(headImageUrl).placeholder(R.drawable.em_login_logo).into(headImageView);
            }
        }else if((requestCode == 2 && resultCode == RESULT_OK)) {
            if(data != null) {
                nickName = data.getStringExtra("nickName");
            }
        }
    }

    private void intSelfDate(){
        String[] userId = new String[1];
        userId[0] = EMClient.getInstance().getCurrentUser();
        EMUserInfoType [] userInfoTypes = new EMUserInfoType[2];
        userInfoTypes[0] = EMUserInfoType.NICKNAME;
        userInfoTypes[1] = EMUserInfoType.AVATAR_URL;
        EMClient.getInstance().userInfoManager().fetchUserInfoByAttribute(userId, userInfoTypes,new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> userInfos) {
                runOnUiThread(new Runnable() {
                    public void run() {
                       EMUserInfo userInfo = userInfos.get(EMClient.getInstance().getCurrentUser());

                        //昵称
                        if(userInfo != null && userInfo.getNickName() != null &&
                                userInfo.getNickName().length() > 0){
                            nickName = userInfo.getNickName();
                            PreferenceManager.getInstance().setCurrentUserNick(nickName);
                        }
                        //头像
                        if(userInfo != null && userInfo.getAvatarUrl() != null && userInfo.getAvatarUrl().length() > 0){
                            headImageUrl = userInfo.getAvatarUrl();
                            Glide.with(mContext).load(headImageUrl).placeholder(R.drawable.em_login_logo).into(headImageView);
                            PreferenceManager.getInstance().setCurrentUserAvatar(headImageUrl);
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e(TAG,"fetchUserInfoByIds error:" + error + " errorMsg:" + errorMsg);
            }
        });

    }

    protected void addLiveDataObserver() {
        LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                Glide.with(mContext).load(event.message).placeholder(R.drawable.em_login_logo).into(headImageView);
            }
        });
        LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                nickName = event.message;
            }
        });
    }
    public void sendOnChannel1 (View v) {
        String title = "Notification";
        String message = "Your profile photo is changed";

        Intent activityIntent = new Intent(this, UserDetailActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_send_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        notificationManager.notify(1, notification);
    }
}
