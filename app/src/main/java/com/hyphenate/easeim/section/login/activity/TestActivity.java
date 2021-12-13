package com.hyphenate.easeim.section.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.easeim.R;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.section.base.BaseInitActivity;

public class TestActivity extends BaseInitActivity implements View.OnClickListener {
    private Button btn_success_1;
    private Button btn_success_2;
    private Button btn_fail_1;
    private Button btn_fail_2;
    private Button btn_default;
    private Button btn_default_thread;
    private Button btn_success_3;
    private Button btn_success_4;

    public static void startAction(Context context) {
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_test;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        btn_success_1 = findViewById(R.id.btn_success_1);
        btn_success_2 = findViewById(R.id.btn_success_2);
        btn_fail_1 = findViewById(R.id.btn_fail_1);
        btn_fail_2 = findViewById(R.id.btn_fail_2);
        btn_default = findViewById(R.id.btn_default);
        btn_default_thread = findViewById(R.id.btn_default_thread);
        btn_success_3 = findViewById(R.id.btn_success_3);
        btn_success_4 = findViewById(R.id.btn_success_4);
    }

    @Override
    protected void initListener() {
        super.initListener();
        btn_success_1.setOnClickListener(this);
        btn_success_2.setOnClickListener(this);
        btn_fail_1.setOnClickListener(this);
        btn_fail_2.setOnClickListener(this);
        btn_default.setOnClickListener(this);
        btn_default_thread.setOnClickListener(this);
        btn_success_3.setOnClickListener(this);
        btn_success_4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_success_3:
                ToastUtils.showSuccessToast(R.string.em_login_btn, R.string.em_error_network_error);
                break;
            case R.id.btn_success_4:
                ToastUtils.showSuccessToast(R.string.em_error_network_error);
                break;
        }
    }


}
