package com.ldq.connect.QQUtils;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.connect.MHookEnvironment;

public class QQSessionUtils {
    public static String GetCurrentGroupUin()
    {
        try {
            int SessionType = (int) FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
            if(SessionType==0)return "";
            if(SessionType==1)return (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
            if(SessionType==1000)return (String) FieldTable.SessionInfo_InTroopUin().get(MHookEnvironment.CurrentSession);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static String GetCurrentFriendUin()
    {
        try {
            int SessionType = (int)FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
            if(SessionType==0)return (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
            if(SessionType==1)return "";
            if(SessionType==1000)return (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static int GetSessionID()
    {
        try {
            return (int) FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
        } catch (Exception e) {
            return -1;
        }
    }

    public static String GetCurrentGuildID(){
        try {
            int SessionType = (int)FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
            if(SessionType==10014)return (String) FieldTable.SessionInfo_ChannelID().get(MHookEnvironment.CurrentSession);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static String GetCurrentChannelCharID(){
        try {
            int SessionType = (int)FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
            if(SessionType==10014)return (String) FieldTable.SessionInfo_ChannelCharID().get(MHookEnvironment.CurrentSession);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
