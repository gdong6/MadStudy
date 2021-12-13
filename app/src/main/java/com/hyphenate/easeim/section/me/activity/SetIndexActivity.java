package com.hyphenate.easeim.section.me.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.widget.ArrowItemView;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.login.activity.LoginActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class SetIndexActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemSecurity;
    private ArrowItemView itemNotification;
    private ArrowItemView itemCommonSet;
    private ArrowItemView itemPrivacy;
    private Button btnLogout;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SetIndexActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_set_index;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemNotification = findViewById(R.id.item_notification);
        itemCommonSet = findViewById(R.id.item_common_set);
        itemPrivacy = findViewById(R.id.item_privacy);
        btnLogout = findViewById(R.id.btn_logout);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemNotification.setOnClickListener(this);
        itemCommonSet.setOnClickListener(this);
        itemPrivacy.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_notification ://消息设置
                MessageReceiveSetActivity.actionStart(mContext);
                break;
            case R.id.item_common_set ://通用
                CommonSettingsActivity.actionStart(mContext);
                break;
            case R.id.item_privacy ://隐私
                PrivacyIndexActivity.actionStart(mContext);
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(mContext);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true,new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
                        finishOtherActivities();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(mContext, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
