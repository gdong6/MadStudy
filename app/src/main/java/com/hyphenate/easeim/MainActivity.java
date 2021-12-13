package com.hyphenate.easeim;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.chat.EMUserInfo.EMUserInfoType;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.enums.SearchType;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.permission.PermissionsManager;
import com.hyphenate.easeim.common.permission.PermissionsResultAction;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.section.MainViewModel;
import com.hyphenate.easeim.section.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.ChatPresenter;
import com.hyphenate.easeim.section.contact.activity.AddContactActivity;
import com.hyphenate.easeim.section.contact.activity.GroupContactManageActivity;
import com.hyphenate.easeim.section.contact.fragment.ContactListFragment;
import com.hyphenate.easeim.section.contact.viewmodels.ContactsViewModel;
import com.hyphenate.easeim.section.conversation.ConversationListFragment;
import com.hyphenate.easeim.section.group.activity.GroupPrePickActivity;
import com.hyphenate.easeim.section.me.AboutMeFragment;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;

import java.lang.reflect.Method;
import java.util.Map;


public class MainActivity extends BaseInitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private EaseTitleBar mTitleBar;
    private EaseBaseFragment mConversationListFragment, mFriendsFragment, mAboutMeFragment;
    private EaseBaseFragment mCurrentFragment;
    private TextView mTvMainHomeMsg, mTvMainFriendsMsg, mTvMainAboutMeMsg;
    private int[] badgeIds = {R.layout.demo_badge_home, R.layout.demo_badge_friends, R.layout.demo_badge_discover, R.layout.demo_badge_about_me};
    private int[] msgIds = {R.id.tv_main_home_msg, R.id.tv_main_friends_msg, R.id.tv_main_discover_msg, R.id.tv_main_about_me_msg};
    private MainViewModel viewModel;
    private boolean showMenu = true;

    public static void startAction(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_main;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mCurrentFragment != null) {
            if(mCurrentFragment instanceof ContactListFragment) {
                menu.findItem(R.id.action_group).setVisible(true);
                menu.findItem(R.id.action_friend).setVisible(false);
                menu.findItem(R.id.action_search_friend).setVisible(true);
                menu.findItem(R.id.action_search_group).setVisible(false);
            }else {
                menu.findItem(R.id.action_group).setVisible(
                        true
                );
                menu.findItem(R.id.action_friend).setVisible(true);
                menu.findItem(R.id.action_search_friend).setVisible(false);
                menu.findItem(R.id.action_search_group).setVisible(false);
            }
        }
        return showMenu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo_conversation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_group :
                GroupPrePickActivity.actionStart(mContext);
                break;
            case R.id.action_friend :
                case R.id.action_search_friend :
                    AddContactActivity.startAction(mContext, SearchType.CHAT);
                    break;
                case R.id.action_search_group :
                    GroupContactManageActivity.actionStart(mContext, true);
                    break;
        }
        return true;
    }

    /**
     * Show menu icon
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(menu != null) {
            if(menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        navView = findViewById(R.id.nav_view);
        mTitleBar = findViewById(R.id.title_bar_main);
        navView.setItemIconTintList(null);
        switchToHome();
        checkIfShowSavedFragment(savedInstanceState);
        addTabBadge();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initViewModel();
        requestPermissions();
        checkUnreadMsg();
        ChatPresenter.getInstance().init();


        if(DemoHelper.getInstance().getModel().isUseFCM() && GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
            if(!FirebaseMessaging.getInstance().isAutoInitEnabled()){
                FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
            }

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        EMLog.d("FCM", "Fetching FCM registration token failed:"+task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    EMLog.d("FCM", token);
                    EMClient.getInstance().sendFCMTokenToServer(token);
                }
            });
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(MainViewModel.class);
        viewModel.getSwitchObservable().observe(this, response -> {
            if(response == null || response == 0) {
                return;
            }
            if(response == R.string.em_main_title_me) {
                mTitleBar.setVisibility(View.GONE);
            }else {
                mTitleBar.setVisibility(View.VISIBLE);
                mTitleBar.setTitle(getResources().getString(response));
            }
        });

        viewModel.homeUnReadObservable().observe(this, readCount -> {
            if(!TextUtils.isEmpty(readCount)) {
                mTvMainHomeMsg.setVisibility(View.VISIBLE);
                mTvMainHomeMsg.setText(readCount);
            }else {
                mTvMainHomeMsg.setVisibility(View.GONE);
            }
        });

        ContactsViewModel contactsViewModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
        contactsViewModel.loadContactList(true);

        viewModel.messageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);

        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(this, this::checkUnReadMsg);

    }

    private void checkUnReadMsg(EaseEvent event) {
        if(event == null) {
            return;
        }
        viewModel.checkUnreadMsg();
    }

    private void addTabBadge() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        int childCount = menuView.getChildCount();
        Log.e("TAG", "bottom child count = "+childCount);
        BottomNavigationItemView itemTab;
        for(int i = 0; i < childCount; i++) {
            itemTab = (BottomNavigationItemView) menuView.getChildAt(i);
            View badge = LayoutInflater.from(mContext).inflate(badgeIds[i], menuView, false);
            switch (i) {
                case 0 :
                    mTvMainHomeMsg = badge.findViewById(msgIds[0]);
                    break;
                case 1 :
                    mTvMainFriendsMsg = badge.findViewById(msgIds[1]);
                    break;
                case 2 :
                    mTvMainAboutMeMsg = badge.findViewById(msgIds[2]);
                    break;
            }
            itemTab.addView(badge);
        }
    }

    private void checkIfShowSavedFragment(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            String tag = savedInstanceState.getString("tag");
            if(!TextUtils.isEmpty(tag)) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if(fragment instanceof EaseBaseFragment) {
                    replace((EaseBaseFragment) fragment, tag);
                }
            }
        }
    }


    private void requestPermissions() {
        PermissionsManager.getInstance()
                .requestAllManifestPermissionsIfNecessary(mContext, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
    }

    private void switchToHome() {
        if(mConversationListFragment == null) {
            mConversationListFragment = new ConversationListFragment();
        }
        replace(mConversationListFragment, "conversation");
    }

    private void switchToFriends() {
        if(mFriendsFragment == null) {
            mFriendsFragment = new ContactListFragment();
        }
        replace(mFriendsFragment, "contact");
    }

    private void switchToAboutMe() {
        if(mAboutMeFragment == null) {
            mAboutMeFragment = new AboutMeFragment();
        }
        fetchSelfInfo();
        replace(mAboutMeFragment, "me");
    }

    private void replace(EaseBaseFragment fragment, String tag) {
        if(mCurrentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if(mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if(!fragment.isAdded()) {
                t.add(R.id.fl_main_fragment, fragment, tag).show(fragment).commit();
            }else {
                t.show(fragment).commit();
            }
        }
    }

    private void fetchSelfInfo(){
        String[] userId = new String[1];
        userId[0] = EMClient.getInstance().getCurrentUser();
        EMUserInfoType[] userInfoTypes = new EMUserInfoType[2];
        userInfoTypes[0] = EMUserInfoType.NICKNAME;
        userInfoTypes[1] = EMUserInfoType.AVATAR_URL;
        EMClient.getInstance().userInfoManager().fetchUserInfoByAttribute(userId, userInfoTypes,new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> userInfos) {
                runOnUiThread(new Runnable() {
                    public void run() {
                       EMUserInfo userInfo = userInfos.get(EMClient.getInstance().getCurrentUser());
                        if(userInfo != null && userInfo.getNickName() != null &&
                                userInfo.getNickName().length() > 0){
                            EaseEvent event = EaseEvent.create(DemoConstant.NICK_NAME_CHANGE, EaseEvent.TYPE.CONTACT);
                            event.message = userInfo.getNickName();
                            LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE).postValue(event);
                            PreferenceManager.getInstance().setCurrentUserNick(userInfo.getNickName());
                        }
                        if(userInfo != null && userInfo.getAvatarUrl() != null && userInfo.getAvatarUrl().length() > 0){

                            EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                            event.message = userInfo.getAvatarUrl();
                            LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
                            PreferenceManager.getInstance().setCurrentUserAvatar(userInfo.getAvatarUrl());
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e("NoteMainActivity","fetchUserInfoByIds error:" + error + " errorMsg:" + errorMsg);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mTitleBar.setVisibility(View.VISIBLE);
        showMenu = true;
        boolean showNavigation = false;
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_home :
                switchToHome();
                mTitleBar.setTitle("Home");
                showNavigation = true;
                break;
            case R.id.em_main_nav_friends :
                switchToFriends();
                mTitleBar.setTitle("Contacts");
                showNavigation = true;
                invalidateOptionsMenu();
                break;
            case R.id.em_main_nav_me :
                switchToAboutMe();
                mTitleBar.setTitle("Me");
                showMenu = false;
                showNavigation = true;
                break;
        }
        invalidateOptionsMenu();
        return showNavigation;
    }

    private void checkUnreadMsg() {
        viewModel.checkUnreadMsg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DemoHelper.getInstance().showNotificationPermissionDialog();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mCurrentFragment != null) {
            outState.putString("tag", mCurrentFragment.getTag());
        }
    }
}
