package com.hyphenate.easeim.section.group;

import android.text.TextUtils;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeim.DemoHelper;

import java.util.List;

public class GroupHelper {


    public static boolean isOwner(EMGroup group) {
        if(group == null ||
                TextUtils.isEmpty(group.getOwner())) {
            return false;
        }
        return TextUtils.equals(group.getOwner(), DemoHelper.getInstance().getCurrentUser());
    }

    public static boolean isOwner(EMChatRoom room) {
        if(room == null ||
                TextUtils.isEmpty(room.getOwner())) {
            return false;
        }
        return TextUtils.equals(room.getOwner(), DemoHelper.getInstance().getCurrentUser());
    }

    public synchronized static boolean isAdmin(EMGroup group) {
        List<String> adminList = group.getAdminList();
        if(adminList != null && !adminList.isEmpty()) {
            return adminList.contains(DemoHelper.getInstance().getCurrentUser());
        }
        return false;
    }

    public synchronized static boolean isAdmin(EMChatRoom group) {
        List<String> adminList = group.getAdminList();
        if(adminList != null && !adminList.isEmpty()) {
            return adminList.contains(DemoHelper.getInstance().getCurrentUser());
        }
        return false;
    }


    public static boolean isCanInvite(EMGroup group) {
        return group != null && (group.isMemberAllowToInvite() || isOwner(group) || isAdmin(group));
    }


    public static boolean isInAdminList(String username, List<String> adminList) {
        return isInList(username, adminList);
    }


    public static boolean isInBlackList(String username, List<String> blackMembers) {
        return isInList(username, blackMembers);
    }


    public static boolean isInMuteList(String username, List<String> muteMembers) {
        return isInList(username, muteMembers);
    }


    public static boolean isInList(String name, List<String> list) {
        if(list == null) {
            return false;
        }
        synchronized (GroupHelper.class) {
            for (String item : list) {
                if (TextUtils.equals(name, item)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String getGroupName(String groupId) {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
        if(group == null) {
            return groupId;
        }
        return TextUtils.isEmpty(group.getGroupName()) ? groupId : group.getGroupName();
    }


    public static boolean isJoinedGroup(List<EMGroup> allJoinGroups, String groupId) {
        if(allJoinGroups == null || allJoinGroups.isEmpty()) {
            return false;
        }
        for (EMGroup group : allJoinGroups) {
            if(TextUtils.equals(group.getGroupId(), groupId)) {
                return true;
            }
        }
        return false;
    }
}
