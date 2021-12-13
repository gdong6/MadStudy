package com.hyphenate.easeim.common.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.db.dao.AppKeyDao;
import com.hyphenate.easeim.common.db.dao.EmUserDao;
import com.hyphenate.easeim.common.db.entity.AppKeyEntity;
import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeim.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.easeim.common.manager.OptionsHelper;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EasePreferenceManager;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoModel {
    EmUserDao dao = null;
    protected Context context = null;
    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();
    public List<EMChatRoom> chatRooms;

    //用户属性数据过期时间设置
    public static long userInfoTimeOut =  7 * 24 * 60 * 60 * 1000;
    
    public DemoModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }

    public long getUserInfoTimeOut() {
        return userInfoTimeOut;
    }

    public void setUserInfoTimeOut(long userInfoTimeOut) {
        if(userInfoTimeOut > 0){
            this.userInfoTimeOut = userInfoTimeOut;
        }
    }


    public boolean updateContactList(List<EaseUser> contactList) {
        List<EmUserEntity> userEntities = EmUserEntity.parseList(contactList);
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao != null) {
            dao.insert(userEntities);
            return true;
        }
        return false;
    }

    public Map<String, EaseUser> getContactList() {
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<EaseUser> users = dao.loadAllContactUsers();
        if(users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }


    public Map<String, EaseUser> getAllUserList() {
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<EaseUser> users = dao.loadAllEaseUsers();
        if(users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }


    public Map<String, EaseUser> getFriendContactList() {
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<EaseUser> users = dao.loadContacts();
        if(users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }

    public boolean isContact(String userId) {
        Map<String, EaseUser> contactList = getFriendContactList();
        return contactList.keySet().contains(userId);
    }
    
    public void saveContact(EaseUser user){
        EmUserDao dao = DemoDbHelper.getInstance(context).getUserDao();
        if(dao == null) {
            return;
        }
        dao.insert(EmUserEntity.parseParent(user));
    }

    public List<AppKeyEntity> getAppKeys() {
        AppKeyDao dao = DemoDbHelper.getInstance(context).getAppKeyDao();
        if(dao == null) {
            return new ArrayList<>();
        }
        String defAppkey = OptionsHelper.getInstance().getDefAppkey();
        String appKey = EMClient.getInstance().getOptions().getAppKey();
        if(!TextUtils.equals(defAppkey, appKey)) {
            List<AppKeyEntity> appKeys = dao.queryKey(appKey);
            if(appKeys == null || appKeys.isEmpty()) {
                dao.insert(new AppKeyEntity(appKey));
            }
        }
        return dao.loadAllAppKeys();
    }

    public void saveAppKey(String appKey) {
        AppKeyDao dao = DemoDbHelper.getInstance(context).getAppKeyDao();
        if(dao == null) {
            return;
        }
        AppKeyEntity entity = new AppKeyEntity(appKey);
        dao.insert(entity);
    }

    public void deleteAppKey(String appKey) {
        AppKeyDao dao = DemoDbHelper.getInstance(context).getAppKeyDao();
        if(dao == null) {
            return;
        }
        dao.deleteAppKey(appKey);
    }

    public DemoDbHelper getDbHelper() {
        return DemoDbHelper.getInstance(DemoApplication.getInstance());
    }

    public void insert(Object object) {
        DemoDbHelper dbHelper = getDbHelper();
        if(object instanceof InviteMessage) {
            if(dbHelper.getInviteMessageDao() != null) {
                dbHelper.getInviteMessageDao().insert((InviteMessage) object);
            }
        }else if(object instanceof MsgTypeManageEntity) {
            if(dbHelper.getMsgTypeManageDao() != null) {
                dbHelper.getMsgTypeManageDao().insert((MsgTypeManageEntity) object);
            }
        }else if(object instanceof EmUserEntity) {
            if(dbHelper.getUserDao() != null) {
                dbHelper.getUserDao().insert((EmUserEntity) object);
            }
        }
    }


    public void update(Object object) {
        DemoDbHelper dbHelper = getDbHelper();
        if(object instanceof InviteMessage) {
            if(dbHelper.getInviteMessageDao() != null) {
                dbHelper.getInviteMessageDao().update((InviteMessage) object);
            }
        }else if(object instanceof MsgTypeManageEntity) {
            if(dbHelper.getMsgTypeManageDao() != null) {
                dbHelper.getMsgTypeManageDao().update((MsgTypeManageEntity) object);
            }
        }else if(object instanceof EmUserEntity) {
            if(dbHelper.getUserDao() != null) {
                dbHelper.getUserDao().insert((EmUserEntity) object);
            }
        }
    }


    public List<String> selectTimeOutUsers() {
        DemoDbHelper dbHelper = getDbHelper();
        List<String> users = null;
        if(dbHelper.getUserDao() != null) {
            users = dbHelper.getUserDao().loadTimeOutEaseUsers(userInfoTimeOut,System.currentTimeMillis());
        }
        return users;
    }

    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsername(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }

    public void deleteUsername(String username, boolean isDelete) {
        SharedPreferences sp = context.getSharedPreferences("save_delete_username_status", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(username, isDelete);
        edit.commit();
    }

    public boolean isDeleteUsername(String username) {
        SharedPreferences sp = context.getSharedPreferences("save_delete_username_status", Context.MODE_PRIVATE);
        return sp.getBoolean(username, false);
    }

    public void setCurrentUserPwd(String pwd) {
        PreferenceManager.getInstance().setCurrentUserPwd(pwd);
    }

    public String getCurrentUserPwd(){
        return PreferenceManager.getInstance().getCurrentUserPwd();
    }

    public void setCurrentUserNick(String nickname) {
        PreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    public String getCurrentUserNick() {
        return PreferenceManager.getInstance().getCurrentUserNick();
    }

    private void setCurrentUserAvatar(String avatar) {
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    private String getCurrentUserAvatar() {
        return PreferenceManager.getInstance().getCurrentUserAvatar();
    }
    
    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {        
        Object val = valueCache.get(Key.SpakerOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }


    public void setDisabledGroups(List<String> groups){
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        List<String> list = new ArrayList<String>();
//        list.addAll(groups);
//        for(int i = 0; i < list.size(); i++){
//            if(EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))){
//                list.remove(i);
//                i--;
//            }
//        }
//
//        dao.setDisabledGroups(list);
//        valueCache.put(Key.DisabledGroups, list);
    }
    
    public List<String> getDisabledGroups(){
        Object val = valueCache.get(Key.DisabledGroups);

//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        if(val == null){
//            val = dao.getDisabledGroups();
//            valueCache.put(Key.DisabledGroups, val);
//        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setDisabledIds(List<String> ids){
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        dao.setDisabledIds(ids);
//        valueCache.put(Key.DisabledIds, ids);
    }
    
    public List<String> getDisabledIds(){
        Object val = valueCache.get(Key.DisabledIds);
        
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        if(val == null){
//            val = dao.getDisabledIds();
//            valueCache.put(Key.DisabledIds, val);
//        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setGroupsSynced(boolean synced){
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }
    
    public boolean isGroupsSynced(){
        return PreferenceManager.getInstance().isGroupsSynced();
    }
    
    public void setContactSynced(boolean synced){
        PreferenceManager.getInstance().setContactSynced(synced);
    }
    
    public boolean isContactSynced(){
        return PreferenceManager.getInstance().isContactSynced();
    }
    
    public void setBlacklistSynced(boolean synced){
        PreferenceManager.getInstance().setBlacklistSynced(synced);
    }
    
    public boolean isBacklistSynced(){
        return PreferenceManager.getInstance().isBacklistSynced();
    }
    

    public void setAdaptiveVideoEncode(boolean value) {
        PreferenceManager.getInstance().setAdaptiveVideoEncode(value);
    }
    
    public boolean isAdaptiveVideoEncode() {
        return PreferenceManager.getInstance().isAdaptiveVideoEncode();
    }

    public void setPushCall(boolean value) {
        PreferenceManager.getInstance().setPushCall(value);
    }

    public boolean isPushCall() {
        return PreferenceManager.getInstance().isPushCall();
    }

    public boolean isMsgRoaming() {
        return PreferenceManager.getInstance().isMsgRoaming();
    }

    public void setMsgRoaming(boolean roaming) {
        PreferenceManager.getInstance().setMsgRoaming(roaming);
    }

    public boolean isShowMsgTyping() {
        return PreferenceManager.getInstance().isShowMsgTyping();
    }

    public void showMsgTyping(boolean show) {
        PreferenceManager.getInstance().showMsgTyping(show);
    }


    public DemoServerSetBean getDefServerSet() {
        return OptionsHelper.getInstance().getDefServerSet();
    }


    public void setUseFCM(boolean useFCM) {
        PreferenceManager.getInstance().setUseFCM(useFCM);
    }


    public boolean isUseFCM() {
        return PreferenceManager.getInstance().isUseFCM();
    }


    public boolean isCustomServerEnable() {
        return OptionsHelper.getInstance().isCustomServerEnable();
    }


    public void enableCustomServer(boolean enable){
        OptionsHelper.getInstance().enableCustomServer(enable);
    }

    public boolean isCustomSetEnable() {
        return OptionsHelper.getInstance().isCustomSetEnable();
    }


    public void enableCustomSet(boolean enable){
        OptionsHelper.getInstance().enableCustomSet(enable);
    }

    public void setRestServer(String restServer){
        OptionsHelper.getInstance().setRestServer(restServer);
    }

    public String getRestServer(){
        return  OptionsHelper.getInstance().getRestServer();
    }

    public void setIMServer(String imServer){
        OptionsHelper.getInstance().setIMServer(imServer);
    }


    public String getIMServer(){
        return OptionsHelper.getInstance().getIMServer();
    }

    public void setIMServerPort(int port) {
        OptionsHelper.getInstance().setIMServerPort(port);
    }

    public int getIMServerPort() {
        return OptionsHelper.getInstance().getIMServerPort();
    }

    public void enableCustomAppkey(boolean enable) {
        OptionsHelper.getInstance().enableCustomAppkey(enable);
    }
    public boolean isCustomAppkeyEnabled() {
        return OptionsHelper.getInstance().isCustomAppkeyEnabled();
    }

    public void setCustomAppkey(String appkey) {
        OptionsHelper.getInstance().setCustomAppkey(appkey);
    }

    public String getCutomAppkey() {
        return OptionsHelper.getInstance().getCustomAppkey();
    }

    public void allowChatroomOwnerLeave(boolean value){
        OptionsHelper.getInstance().allowChatroomOwnerLeave(value);
    }

    public boolean isChatroomOwnerLeaveAllowed(){
        return OptionsHelper.getInstance().isChatroomOwnerLeaveAllowed();
    }

    public void setDeleteMessagesAsExitGroup(boolean value) {
        OptionsHelper.getInstance().setDeleteMessagesAsExitGroup(value);
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return OptionsHelper.getInstance().isDeleteMessagesAsExitGroup();
    }

    public void setDeleteMessagesAsExitChatRoom(boolean value) {
        OptionsHelper.getInstance().setDeleteMessagesAsExitChatRoom(value);
    }

    public boolean isDeleteMessagesAsExitChatRoom() {
        return OptionsHelper.getInstance().isDeleteMessagesAsExitChatRoom();
    }
    public void setAutoAcceptGroupInvitation(boolean value) {
        OptionsHelper.getInstance().setAutoAcceptGroupInvitation(value);
    }

    public boolean isAutoAcceptGroupInvitation() {
        return OptionsHelper.getInstance().isAutoAcceptGroupInvitation();
    }


    public void setTransfeFileByUser(boolean value) {
        OptionsHelper.getInstance().setTransfeFileByUser(value);
    }


    public boolean isSetTransferFileByUser() {
        return OptionsHelper.getInstance().isSetTransferFileByUser();
    }


    public void setAutodownloadThumbnail(boolean autodownload) {
        OptionsHelper.getInstance().setAutodownloadThumbnail(autodownload);
    }


    public boolean isSetAutodownloadThumbnail() {
        return OptionsHelper.getInstance().isSetAutodownloadThumbnail();
    }



    public void setUsingHttpsOnly(boolean usingHttpsOnly) {
        OptionsHelper.getInstance().setUsingHttpsOnly(usingHttpsOnly);
    }


    public boolean getUsingHttpsOnly() {
        return OptionsHelper.getInstance().getUsingHttpsOnly();
    }

    public void setSortMessageByServerTime(boolean sortByServerTime) {
        OptionsHelper.getInstance().setSortMessageByServerTime(sortByServerTime);
    }

    public boolean isSortMessageByServerTime() {
        return OptionsHelper.getInstance().isSortMessageByServerTime();
    }


    public void setEnableTokenLogin(boolean isChecked) {
        PreferenceManager.getInstance().setEnableTokenLogin(isChecked);
    }

    public boolean isEnableTokenLogin() {
        return PreferenceManager.getInstance().isEnableTokenLogin();
    }


    public void saveUnSendMsg(String toChatUsername, String content) {
        EasePreferenceManager.getInstance().saveUnSendMsgInfo(toChatUsername, content);
    }

    public String getUnSendMsg(String toChatUsername) {
        return EasePreferenceManager.getInstance().getUnSendMsgInfo(toChatUsername);
    }


    public boolean isFirstInstall() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_first_install", true);
    }


    public void makeNotFirstInstall() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("is_first_install", false).apply();
        preferences.edit().putBoolean("is_conversation_come_from_server", true).apply();
    }


    public boolean isConComeFromServer() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_conversation_come_from_server", false);
    }


    public void modifyConComeFromStatus() {
        SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("is_conversation_come_from_server", false).apply();
    }

    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
