package com.hyphenate.easeim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMChatRoomManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMPushManager;
import com.hyphenate.cloud.EMHttpClient;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallEndReason;
import com.hyphenate.easecallkit.base.EaseCallKitConfig;
import com.hyphenate.easecallkit.base.EaseCallKitListener;
import com.hyphenate.easecallkit.base.EaseCallKitTokenCallback;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.base.EaseGetUserAccountCallback;
import com.hyphenate.easecallkit.base.EaseUserAccount;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.manager.UserProfileManager;
import com.hyphenate.easeim.common.model.DemoModel;
import com.hyphenate.easeim.common.model.EmojiconExampleGroupData;
import com.hyphenate.easeim.common.receiver.HeadsetReceiver;
import com.hyphenate.easeim.common.utils.FetchUserInfoList;
import com.hyphenate.easeim.common.utils.FetchUserRunnable;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.section.chat.ChatPresenter;
import com.hyphenate.easeim.section.chat.delegates.ChatConferenceInviteAdapterDelegate;
import com.hyphenate.easeim.section.chat.delegates.ChatNotificationAdapterDelegate;
import com.hyphenate.easeim.section.chat.delegates.ChatRecallAdapterDelegate;
import com.hyphenate.easeim.section.chat.delegates.ChatUserCardAdapterDelegate;
import com.hyphenate.easeim.section.conference.ConferenceInviteActivity;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.delegate.EaseCustomAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseExpressionAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseFileAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseImageAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseLocationAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseTextAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseVideoAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseVoiceAdapterDelegate;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EaseMessageTypeSetManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.provider.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.provider.EaseSettingsProvider;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.push.EMPushConfig;
import com.hyphenate.push.EMPushHelper;
import com.hyphenate.push.EMPushType;
import com.hyphenate.push.PushListener;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DemoHelper {
    private static final String TAG = DemoHelper.class.getSimpleName();

    public boolean isSDKInit;
    private static DemoHelper mInstance;
    private DemoModel demoModel = null;
    private Map<String, EaseUser> contactList;
    private UserProfileManager userProManager;

    private EaseCallKitListener callKitListener;
    private Context mianContext;

    private String tokenUrl = "http://a1.easemob.com/token/rtcToken/v1";
    private String uIdUrl = "http://a1.easemob.com/channel/mapper";
    
    private FetchUserRunnable fetchUserRunnable;
    private Thread fetchUserTread;
    private FetchUserInfoList fetchUserInfoList;


    private DemoHelper() {}

    public static DemoHelper getInstance() {
        if(mInstance == null) {
            synchronized (DemoHelper.class) {
                if(mInstance == null) {
                    mInstance = new DemoHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        demoModel = new DemoModel(context);
        //初始化IM SDK
        if(initSDK(context)) {
            // debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            // set Call options
            setCallOptions(context);
            //初始化推送
            initPush(context);
            //注册call Receiver
            //initReceiver(context);
            //初始化ease ui相关
            initEaseUI(context);
            //注册对话类型
            registerConversationType();

            //callKit初始化
            InitCallKit(context);

            //启动获取用户信息线程
            fetchUserInfoList = FetchUserInfoList.getInstance();
            fetchUserRunnable = new FetchUserRunnable();
            fetchUserTread = new Thread(fetchUserRunnable);
            fetchUserTread.start();
        }

    }


    private void InitCallKit(Context context){
        EaseCallKitConfig callKitConfig = new EaseCallKitConfig();
        callKitConfig.setCallTimeOut(30 * 1000);

        callKitConfig.setAgoraAppId("15cb0d28b87b425ea613fc46f7c9f974");
        callKitConfig.setEnableRTCToken(true);
        EaseCallKit.getInstance().init(context,callKitConfig);

        addCallkitListener();
    }


    private boolean initSDK(Context context) {
        EMOptions options = initChatOptions(context);
        isSDKInit = EaseIM.getInstance().init(context, options);
        demoModel.setUserInfoTimeOut(30 * 60 * 1000);
        updateTimeoutUsers();
        mianContext = context;
        return isSDKInit();
    }



    private void registerConversationType() {
        EaseMessageTypeSetManager.getInstance()
                .addMessageType(EaseExpressionAdapterDelegate.class)
                .addMessageType(EaseFileAdapterDelegate.class)
                .addMessageType(EaseImageAdapterDelegate.class)
                .addMessageType(EaseLocationAdapterDelegate.class)
                .addMessageType(EaseVideoAdapterDelegate.class)
                .addMessageType(EaseVoiceAdapterDelegate.class)
                .addMessageType(ChatConferenceInviteAdapterDelegate.class)
                .addMessageType(ChatRecallAdapterDelegate.class)

                .addMessageType(ChatUserCardAdapterDelegate.class)
                .addMessageType(EaseCustomAdapterDelegate.class)
                .addMessageType(ChatNotificationAdapterDelegate.class)
                .setDefaultMessageType(EaseTextAdapterDelegate.class);
    }


    public boolean isLoggedIn() {
        return getEMClient().isLoggedInBefore();
    }


    public EMClient getEMClient() {
        return EMClient.getInstance();
    }


    public EMContactManager getContactManager() {
        return getEMClient().contactManager();
    }


    public EMGroupManager getGroupManager() {
        return getEMClient().groupManager();
    }

    public EMChatRoomManager getChatroomManager() {
        return getEMClient().chatroomManager();
    }



    public EMChatManager getChatManager() {
        return getEMClient().chatManager();
    }

    public EMPushManager getPushManager() {
        return getEMClient().pushManager();
    }


    public EMConversation getConversation(String username, EMConversation.EMConversationType type, boolean createIfNotExists) {
        return getChatManager().getConversation(username, type, createIfNotExists);
    }

    public String getCurrentUser() {
        return getEMClient().getCurrentUser();
    }


    private void initEaseUI(Context context) {

        EaseIM.getInstance().addChatPresenter(ChatPresenter.getInstance());
        EaseIM.getInstance()
                .setSettingsProvider(new EaseSettingsProvider() {
                    @Override
                    public boolean isMsgNotifyAllowed(EMMessage message) {
                        if(message == null){
                            return demoModel.getSettingMsgNotification();
                        }
                        if(!demoModel.getSettingMsgNotification()){
                            return false;
                        }else{
                            String chatUsename = null;
                            List<String> notNotifyIds = null;
                            // get user or group id which was blocked to show message notifications
                            if (message.getChatType() == EMMessage.ChatType.Chat) {
                                chatUsename = message.getFrom();
                                notNotifyIds = demoModel.getDisabledIds();
                            } else {
                                chatUsename = message.getTo();
                                notNotifyIds = demoModel.getDisabledGroups();
                            }

                            if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }

                    @Override
                    public boolean isMsgSoundAllowed(EMMessage message) {
                        return demoModel.getSettingMsgSound();
                    }

                    @Override
                    public boolean isMsgVibrateAllowed(EMMessage message) {
                        return demoModel.getSettingMsgVibrate();
                    }

                    @Override
                    public boolean isSpeakerOpened() {
                        return demoModel.getSettingMsgSpeaker();
                    }
                })
                .setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {
                    @Override
                    public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                        EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                        for(EaseEmojicon emojicon : data.getEmojiconList()){
                            if(emojicon.getIdentityCode().equals(emojiconIdentityCode)){
                                return emojicon;
                            }
                        }
                        return null;
                    }

                    @Override
                    public Map<String, Object> getTextEmojiconMapping() {
                        return null;
                    }
                })
                .setAvatarOptions(getAvatarOptions())
                .setUserProvider(new EaseUserProfileProvider() {
                    @Override
                    public EaseUser getUser(String username) {
                        return getUserInfo(username);
                    }

                });
    }

    private EaseAvatarOptions getAvatarOptions() {
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(1);
        return avatarOptions;
    }

    public EaseUser getUserInfo(String username) {

        EaseUser user = null;
        if(username.equals(EMClient.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentUserInfo();
        user = getContactList().get(username);
        if(user == null){
            updateContactList();
            user = getContactList().get(username);
            if(user == null){
                if(fetchUserInfoList != null){
                    fetchUserInfoList.addUserId(username);
                }
            }
        }
        return user;
    }


    private EMOptions initChatOptions(Context context){
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setRequireAck(true);
        options.setRequireDeliveryAck(false);


        EMPushConfig.Builder builder = new EMPushConfig.Builder(context);

        builder.enableVivoPush()
                .enableMeiZuPush("134952", "f00e7e8499a549e09731a60a4da399e3")
                .enableMiPush("2882303761517426801", "5381742660801")
                .enableOppoPush("0bb597c5e9234f3ab9f821adbeceecdb",
                        "cd93056d03e1418eaa6c3faf10fd7537")
                .enableHWPush()
                .enableFCM("921300338324");
        options.setPushConfig(builder.build());

        if(demoModel.isCustomSetEnable()) {
            if(demoModel.isCustomServerEnable() && demoModel.getRestServer() != null && demoModel.getIMServer() != null) {
                options.setRestServer(demoModel.getRestServer());
                options.setIMServer(demoModel.getIMServer());
                if(demoModel.getIMServer().contains(":")) {
                    options.setIMServer(demoModel.getIMServer().split(":")[0]);
                    options.setImPort(Integer.valueOf(demoModel.getIMServer().split(":")[1]));
                }else {
                    if(demoModel.getIMServerPort() != 0) {
                        options.setImPort(demoModel.getIMServerPort());
                    }
                }
            }
        }
        if (demoModel.isCustomAppkeyEnabled() && !TextUtils.isEmpty(demoModel.getCutomAppkey())) {
            options.setAppKey(demoModel.getCutomAppkey());
        }

        String imServer = options.getImServer();
        String restServer = options.getRestServer();

        options.allowChatroomOwnerLeave(demoModel.isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(demoModel.isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(demoModel.isAutoAcceptGroupInvitation());
        options.setAutoTransferMessageAttachments(demoModel.isSetTransferFileByUser());
        options.setAutoDownloadThumbnail(demoModel.isSetAutodownloadThumbnail());
        return options;
    }

    private void setCallOptions(Context context) {
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        IntentFilter headsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(headsetReceiver, headsetFilter);
    }

    public void initPush(Context context) {
        if(EaseIM.getInstance().isMainProcess(context)) {

            EMPushHelper.getInstance().setPushListener(new PushListener() {
                @Override
                public void onError(EMPushType pushType, long errorCode) {
                    EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
                }

                @Override
                public boolean isSupportPush(EMPushType pushType, EMPushConfig pushConfig) {
                    if(pushType == EMPushType.FCM){
                        EMLog.d("FCM", "GooglePlayServiceCode:"+GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context));
                        return demoModel.isUseFCM() && GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
                    }
                    return super.isSupportPush(pushType, pushConfig);
                }
            });
        }
    }

    /**
     * logout
     *
     * @param unbindDeviceToken
     *            whether you need unbind your device token
     * @param callback
     *            callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        Log.d(TAG, "logout: " + unbindDeviceToken);
        if(fetchUserTread != null && fetchUserRunnable != null){
            fetchUserRunnable.setStop(true);
        }
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                logoutSuccess();
                //reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    public void killApp() {
        List<Activity> activities = DemoApplication.getInstance().getLifecycleCallbacks().getActivityList();
        if(activities != null && !activities.isEmpty()) {
            for(Activity activity : activities) {
                activity.finish();
            }
        }
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    public void logoutSuccess() {
        Log.d(TAG, "logout: onSuccess");
        setAutoLogin(false);
        DemoDbHelper.getInstance(DemoApplication.getInstance()).closeDb();
    }

    public EaseAvatarOptions getEaseAvatarOptions() {
        return EaseIM.getInstance().getAvatarOptions();
    }

    public DemoModel getModel(){
        if(demoModel == null) {
            demoModel = new DemoModel(DemoApplication.getInstance());
        }
        return demoModel;
    }

    public String getCurrentLoginUser() {
        return getModel().getCurrentUsername();
    }

    public EaseNotifier getNotifier(){
        return EaseIM.getInstance().getNotifier();
    }


    public void setAutoLogin(boolean autoLogin) {
        PreferenceManager.getInstance().setAutoLogin(autoLogin);
    }

    public boolean getAutoLogin() {
        return PreferenceManager.getInstance().getAutoLogin();
    }

    public void setSDKInit(boolean init) {
        isSDKInit = init;
    }

    public boolean isSDKInit() {
        return isSDKInit;
    }

    public void insert(Object object) {
        demoModel.insert(object);
    }

    public void update(Object object) {
        demoModel.update(object);
    }

    public void updateUserList(List<EaseUser> users){
        demoModel.updateContactList(users);
    }
    public void updateTimeoutUsers() {
        List<String> userIds = demoModel.selectTimeOutUsers();
        if(userIds != null && userIds.size() > 0){
            if(fetchUserInfoList != null){
                for(int i = 0; i < userIds.size(); i++){
                    fetchUserInfoList.addUserId(userIds.get(i));
                }
            }
        }
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            updateTimeoutUsers();
            contactList = demoModel.getAllUserList();
        }

        // return a empty non-null object to avoid app crash
        if(contactList == null){
            return new Hashtable<String, EaseUser>();
        }
        return contactList;
    }

    /**
     * update contact list
     */
    public void updateContactList() {
        if(isLoggedIn()) {
            updateTimeoutUsers();
            contactList = demoModel.getContactList();
        }
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

    public void showNotificationPermissionDialog() {
        EMPushType pushType = EMPushHelper.getInstance().getPushType();


    }

    public synchronized int deleteContact(String username) {
        if(TextUtils.isEmpty(username)) {
            return 0;
        }
        DemoDbHelper helper = DemoDbHelper.getInstance(DemoApplication.getInstance());
        if(helper.getUserDao() == null) {
            return 0;
        }
        int num = helper.getUserDao().deleteUser(username);
        if(helper.getInviteMessageDao() != null) {
            helper.getInviteMessageDao().deleteByFrom(username);
        }
        EMClient.getInstance().chatManager().deleteConversation(username, false);
        getModel().deleteUsername(username, false);
        Log.e(TAG, "delete num = "+num);
        return num;
    }

    public boolean isFirstInstall() {
        return getModel().isFirstInstall();
    }


    public void makeNotFirstInstall() {
        getModel().makeNotFirstInstall();
    }


    public boolean isConComeFromServer() {
        return getModel().isConComeFromServer();
    }


    public boolean isCurrentUserFromOtherDevice(String username) {
        if(TextUtils.isEmpty(username)) {
            return false;
        }
        if(username.contains("/") && username.contains(EMClient.getInstance().getCurrentUser())) {
            return true;
        }
        return false;
    }


    public void addCallkitListener(){
        callKitListener = new EaseCallKitListener() {
            @Override
            public void onInviteUsers(Context context,String userId[],JSONObject ext) {
                Intent intent = new Intent(context, ConferenceInviteActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                String groupId = null;
                if(ext != null && ext.length() > 0){
                    try {
                        groupId = ext.getString("groupId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID, groupId);
                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_EXIST_MEMBERS, userId);
                context.startActivity(intent);
            }

            @Override
            public void onEndCallWithReason(EaseCallType callType, String channelName, EaseCallEndReason reason, long callTime) {
                EMLog.d(TAG,"onEndCallWithReason" + (callType != null ? callType.name() : " callType is null ") + " reason:" + reason + " time:"+ callTime);
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String callString = "Length ";
                callString += formatter.format(callTime);

                Toast.makeText(mianContext,callString,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGenerateToken(String userId, String channelName, String appKey, EaseCallKitTokenCallback callback){
                EMLog.d(TAG,"onGenerateToken userId:" + userId + " channelName:" + channelName + " appKey:"+ appKey);
                String url = tokenUrl;
                url += "?";
                url += "userAccount=";
                url += userId;
                url += "&channelName=";
                url += channelName;
                url += "&appkey=";
                url +=  appKey;

                getRtcToken(url, callback);
            }

            @Override
            public void onReceivedCall(EaseCallType callType, String fromUserId,JSONObject ext) {

                EMLog.d(TAG,"onReceivedCall" + callType.name() + " fromUserId:" + fromUserId);
            }
            @Override
            public  void onCallError(EaseCallKit.EaseCallError type, int errorCode, String description){

            }

            @Override
            public void onInViteCallMessageSent(){
                LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
            }

            @Override
            public void onRemoteUserJoinChannel(String channelName, String userName, int uid, EaseGetUserAccountCallback callback){
                if(userName == null || userName == ""){
                    String url = uIdUrl;
                    url += "?";
                    url += "channelName=";
                    url += channelName;
                    url += "&userAccount=";
                    url += EMClient.getInstance().getCurrentUser();
                    url += "&appkey=";
                    url +=  EMClient.getInstance().getOptions().getAppKey();
                    getUserIdAgoraUid(uid,url,callback);
                }else{
                    setEaseCallKitUserInfo(userName);
                    EaseUserAccount account = new EaseUserAccount(uid,userName);
                    List<EaseUserAccount> accounts = new ArrayList<>();
                    accounts.add(account);
                    callback.onUserAccount(accounts);
                }
            }
        };
        EaseCallKit.getInstance().setCallKitListener(callKitListener);
    }


    private void getRtcToken(String tokenUrl,EaseCallKitTokenCallback callback){
        new AsyncTask<String, Void, Pair<Integer, String>>(){
            @Override
            protected Pair<Integer, String> doInBackground(String... str) {
                try {
                    Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(tokenUrl, null,EMHttpClient.GET);
                    return response;
                }catch (HyphenateException exception) {
                    exception.printStackTrace();
                }
                return  null;
            }
            @Override
            protected void onPostExecute(Pair<Integer, String> response) {
                if(response != null) {
                    try {
                          int resCode = response.first;
                          if(resCode == 200){
                              String responseInfo = response.second;
                              if(responseInfo != null && responseInfo.length() > 0){
                                  try {
                                      JSONObject object = new JSONObject(responseInfo);
                                      String token = object.getString("accessToken");
                                      int uId = object.getInt("agoraUserId");

                                      setEaseCallKitUserInfo(EMClient.getInstance().getCurrentUser());
                                      callback.onSetToken(token,uId);
                                  }catch (Exception e){
                                      e.getStackTrace();
                                  }
                              }else{
                                  callback.onGetTokenError(response.first,response.second);
                              }
                          }else{
                              callback.onGetTokenError(response.first,response.second);
                          }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    callback.onSetToken(null,0);
                }
            }
        }.execute(tokenUrl);
    }


    private void getUserIdAgoraUid(int uId, String url, EaseGetUserAccountCallback callback){
        new AsyncTask<String, Void, Pair<Integer, String>>(){
            @Override
            protected Pair<Integer, String> doInBackground(String... str) {
                try {
                    Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(url, null,EMHttpClient.GET);
                    return response;
                }catch (HyphenateException exception) {
                    exception.printStackTrace();
                }
                return  null;
            }
            @Override
            protected void onPostExecute(Pair<Integer, String> response) {
                if(response != null) {
                    try {
                        int resCode = response.first;
                        if(resCode == 200){
                            String responseInfo = response.second;
                            List<EaseUserAccount> userAccounts = new ArrayList<>();
                            if(responseInfo != null && responseInfo.length() > 0){
                                try {
                                    JSONObject object = new JSONObject(responseInfo);
                                    JSONObject resToken = object.getJSONObject("result");
                                    Iterator it = resToken.keys();
                                    while(it.hasNext()) {
                                        String uIdStr = it.next().toString();
                                        int uid = 0;
                                        uid = Integer.valueOf(uIdStr).intValue();
                                        String username = resToken.optString(uIdStr);
                                        if(uid == uId){
                                            //获取到当前用户的userName 设置头像昵称等信息
                                            setEaseCallKitUserInfo(username);
                                        }
                                        userAccounts.add(new EaseUserAccount(uid, username));
                                    }
                                    callback.onUserAccount(userAccounts);
                                }catch (Exception e){
                                    e.getStackTrace();
                                }
                            }else{
                                callback.onSetUserAccountError(response.first,response.second);
                            }
                        }else{
                            callback.onSetUserAccountError(response.first,response.second);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    callback.onSetUserAccountError(100,"response is null");
                }
            }
        }.execute(url);
    }


    private void setEaseCallKitUserInfo(String userName){
        EaseUser user = getUserInfo(userName);
        EaseCallUserInfo userInfo = new EaseCallUserInfo();
        if(user != null){
            userInfo.setNickName(user.getNickname());
            userInfo.setHeadImage(user.getAvatar());
        }
        EaseCallKit.getInstance().getCallKitConfig().setUserInfo(userName,userInfo);
    }


    /**
     * data sync listener
     */
    public interface DataSyncListener {
        /**
         * sync complete
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);
    }
}
