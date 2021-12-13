package com.hyphenate.easeim.common.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.common.model.DemoServerSetBean;
import com.hyphenate.easeim.common.utils.AppMetaDataHelper;
import com.hyphenate.easeim.common.utils.PreferenceManager;

public class OptionsHelper {
    private String DEF_APPKEY = "";
    private static final String DEF_IM_SERVER = "msync-im1.sandbox.easemob.com";
    private static final int DEF_IM_PORT = 6717;
    private static final String DEF_REST_SERVER = "a1.sdb.easemob.com";

    private static OptionsHelper instance;

    private OptionsHelper(){getDefaultAppkey();}

    public static OptionsHelper getInstance() {
        if(instance == null) {
            synchronized (OptionsHelper.class) {
                if(instance == null) {
                    instance = new OptionsHelper();
                }
            }
        }
        return instance;
    }

    private void getDefaultAppkey() {
        DEF_APPKEY = AppMetaDataHelper.getInstance().getPlaceholderValue("EASEMOB_APPKEY");
    }



    public boolean isCustomSetEnable() {
        return PreferenceManager.getInstance().isCustomSetEnable();
    }


    public void enableCustomSet(boolean enable){
        PreferenceManager.getInstance().enableCustomSet(enable);
    }


    public boolean isCustomServerEnable() {
        return PreferenceManager.getInstance().isCustomServerEnable();
    }


    public void enableCustomServer(boolean enable){
        PreferenceManager.getInstance().enableCustomServer(enable);
    }

    public void setRestServer(String restServer){
        PreferenceManager.getInstance().setRestServer(restServer);
    }


    public String getRestServer(){
        return  PreferenceManager.getInstance().getRestServer();
    }


    public void setIMServer(String imServer){
        PreferenceManager.getInstance().setIMServer(imServer);
    }


    public String getIMServer(){
        return PreferenceManager.getInstance().getIMServer();
    }


    public void setIMServerPort(int port) {
        PreferenceManager.getInstance().setIMServerPort(port);
    }

    public int getIMServerPort() {
        return PreferenceManager.getInstance().getIMServerPort();
    }


    public void enableCustomAppkey(boolean enable) {
        PreferenceManager.getInstance().enableCustomAppkey(enable);
    }


    public boolean isCustomAppkeyEnabled() {
        return PreferenceManager.getInstance().isCustomAppkeyEnabled();
    }


    public void setCustomAppkey(String appkey) {
        PreferenceManager.getInstance().setCustomAppkey(appkey);
    }


    public String getCustomAppkey() {
        return PreferenceManager.getInstance().getCustomAppkey();
    }



    public void setUsingHttpsOnly(boolean usingHttpsOnly) {
        PreferenceManager.getInstance().setUsingHttpsOnly(usingHttpsOnly);
    }

   
    public boolean getUsingHttpsOnly() {
        return PreferenceManager.getInstance().getUsingHttpsOnly();
    }


    public void allowChatroomOwnerLeave(boolean value){
        PreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }


    public boolean isChatroomOwnerLeaveAllowed(){
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }


    public void setDeleteMessagesAsExitGroup(boolean value) {
        PreferenceManager.getInstance().setDeleteMessagesAsExitGroup(value);
    }


    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }


    public void setDeleteMessagesAsExitChatRoom(boolean value){
        PreferenceManager.getInstance().setDeleteMessagesAsExitChatRoom(value);
    }

    public boolean isDeleteMessagesAsExitChatRoom() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitChatRoom();
    }


    public void setAutoAcceptGroupInvitation(boolean value) {
        PreferenceManager.getInstance().setAutoAcceptGroupInvitation(value);
    }


    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }

    public void setTransfeFileByUser(boolean value) {
        PreferenceManager.getInstance().setTransferFileByUser(value);
    }

    public boolean isSetTransferFileByUser() {
        return PreferenceManager.getInstance().isSetTransferFileByUser();
    }


    public void setAutodownloadThumbnail(boolean autodownload) {
        PreferenceManager.getInstance().setAudodownloadThumbnail(autodownload);
    }


    public boolean isSetAutodownloadThumbnail() {
        return PreferenceManager.getInstance().isSetAutodownloadThumbnail();
    }

    public void setSortMessageByServerTime(boolean sortByServerTime) {
        PreferenceManager.getInstance().setSortMessageByServerTime(sortByServerTime);
    }

    public boolean isSortMessageByServerTime() {
        return PreferenceManager.getInstance().isSortMessageByServerTime();
    }

    public String getDefAppkey() {
        return DEF_APPKEY;
    }

    public String getDefImServer() {
        return DEF_IM_SERVER;
    }

    public int getDefImPort() {
        return DEF_IM_PORT;
    }

    public String getDefRestServer() {
        return DEF_REST_SERVER;
    }

    public DemoServerSetBean getServerSet() {
        DemoServerSetBean bean = new DemoServerSetBean();
        bean.setAppkey(getCustomAppkey());
        bean.setCustomServerEnable(isCustomServerEnable());
        bean.setHttpsOnly(getUsingHttpsOnly());
        bean.setImServer(getIMServer());
        bean.setRestServer(getRestServer());
        return bean;
    }

    public DemoServerSetBean getDefServerSet() {
        DemoServerSetBean bean = new DemoServerSetBean();
        bean.setAppkey(getDefAppkey());
        bean.setRestServer(getDefRestServer());
        bean.setImServer(getDefImServer());
        bean.setImPort(getDefImPort());
        bean.setHttpsOnly(getUsingHttpsOnly());
        bean.setCustomServerEnable(isCustomServerEnable());
        return bean;
    }

}
