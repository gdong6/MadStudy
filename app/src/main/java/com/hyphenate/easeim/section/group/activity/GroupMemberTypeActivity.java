package com.hyphenate.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.widget.ArrowItemView;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.group.viewmodels.GroupMemberAuthorityViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.List;

import androidx.lifecycle.ViewModelProvider;

public class GroupMemberTypeActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemAdmin;
    private ArrowItemView itemMember;
    private String groupId;
    private EMGroup group;
    private boolean isOwner;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat_group_member_type;
    }

    public static void actionStart(Context context, String groupId, boolean owner) {
        Intent starter = new Intent(context, GroupMemberTypeActivity.class);
        starter.putExtra("groupId", groupId);
        starter.putExtra("isOwner", owner);
        context.startActivity(starter);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
        isOwner = intent.getBooleanExtra("isOwner", false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemAdmin = findViewById(R.id.item_admin);
        itemMember = findViewById(R.id.item_member);

        group = DemoHelper.getInstance().getGroupManager().getGroup(groupId);

        initGroupData(group);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemAdmin.setOnClickListener(this);
        itemMember.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        GroupMemberAuthorityViewModel viewModel = new ViewModelProvider(this).get(GroupMemberAuthorityViewModel.class);
        viewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    initGroupData(data);
                }
            });
        });
        viewModel.getMemberObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    setMemberCount(data.size());
                }
            });
        });
        viewModel.getMessageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event.isGroupChange()) {
                viewModel.getGroup(groupId);
                viewModel.getMembers(groupId);
            }else if(event.isGroupLeave() && TextUtils.equals(groupId, event.message)) {
                finish();
            }
        });
        viewModel.getMembers(groupId);
    }

    private void initGroupData(EMGroup group) {
        setAdminCount(group.getAdminList().size() + 1);
        setMemberCount(group.getMembers().size());
    }

    private void setAdminCount(int count) {
        itemAdmin.getTvContent().setText(getString(R.string.em_group_member_type_member_num, count));
    }

    private void setMemberCount(int count) {
        itemMember.getTvContent().setText(getString(R.string.em_group_member_type_member_num, count));
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.item_member :
                GroupMemberAuthorityActivity.actionStart(mContext, groupId);
                break;
        }
    }
}
