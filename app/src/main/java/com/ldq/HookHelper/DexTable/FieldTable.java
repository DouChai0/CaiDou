package com.ldq.HookHelper.DexTable;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ldq.Utils.MLogCat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class FieldTable {
    private static final int NowQQVersion = VersionUtils.GetQQVersionInt();
    private static final HashMap<String,Field> FieldMap = new HashMap<>(128);
    public static Field BaseChatPie_SessionInfo() {
        String FieldSignKey = "BaseChatPie_SessionInfo";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = FindFirstField(ClassTable.BaseChatPie(),ClassTable.SessionInfo());
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field BaseChatPie_QQAppinterFace() {
        String FieldSignKey = "BaseChatPie_QQAppinterFace";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = FindFirstField(ClassTable.BaseChatPie(),ClassTable.QQAppinterFace());
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field BaseChatPie_HelperProvider() {
        String FieldSignKey = "BaseChatPie_HelperProvider";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = FindFirstField(ClassTable.BaseChatPie(),ClassTable.HelperProvider());
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field BaseChatItemLayout_EdTailView() {
        String FieldSignKey = "BaseChatItemLayout_EdTailView";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.BaseChatItemLayout(),"c", TextView.class) :
                NowQQVersion < 5865 ? FindField(ClassTable.BaseChatItemLayout(),"be",TextView.class) :
                NowQQVersion < 7395 ? FindField(ClassTable.BaseChatItemLayout(),"bf",TextView.class) :
                        FindField(ClassTable.BaseChatItemLayout(),"bh",TextView.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field SessionInfo_isTroop() {
        String FieldSignKey = "SessionInfo_isTroop";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.SessionInfo(),"a", int.class) :
                FindField(ClassTable.SessionInfo(),"a",int.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field SessionInfo_friendUin() {
        String FieldSignKey = "SessionInfo_friendUin";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.SessionInfo(),"a", String.class) :
                FindField(ClassTable.SessionInfo(),"b",String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field SessionInfo_ChannelCharID() {
        return SessionInfo_friendUin();
    }
    public static Field SessionInfo_InTroopUin() {
        String FieldSignKey = "SessionInfo_InTroopUin";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.SessionInfo(),"c", String.class) :
                FindField(ClassTable.SessionInfo(),"d",String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field SessionInfo_ChannelID(){
        return SessionInfo_TroopCode();
    }
    public static Field SessionInfo_TroopCode() {
        String FieldSignKey = "SessionInfo_TroopCode";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.SessionInfo(),"b", String.class) :
                FindField(ClassTable.SessionInfo(),"c",String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field SessionInfo_CodeName() {
        String FieldSignKey = "SessionInfo_TroopCode";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = FindField(ClassTable.BaseSessionInfo(),"e",String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field TroopSettingActivity_TroopInfoData() {
        String FieldSignKey = "TroopSettingActivity_TroopInfoData";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.TroopSettingActivity(),"a", ClassTable.TroopInfoData()) :
                FindField(ClassTable.TroopSettingActivity(),"i",ClassTable.TroopInfoData());
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field GdtMotiveVideoDialog_MyController() {
        String FieldSignKey = "GdtMotiveVideoDialog_MyController";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.GdtMotiveVideoDialog(),"a", ClassTable.GdtMvViewController()) :
                FindField(ClassTable.GdtMotiveVideoDialog(),"c",ClassTable.GdtMvViewController());
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field GdtMvViewController_IsWatchedAds() {
        String FieldSignKey = "GdtMvViewController_IsWatchedAds";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.GdtMvViewController(),"c", boolean.class) :
                FindField(ClassTable.GdtMvViewController(),"m",boolean.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field GdtMotiveBrowsingDialog_IsWatchedAds() {
        String FieldSignKey = "GdtMotiveBrowsingDialog_IsWatchedAds";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.GdtMotiveBrowsingDialog(),"a", boolean.class) :
                FindField(ClassTable.GdtMotiveBrowsingDialog(),"h",boolean.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field RevokeMsgInfo_GroupUin() {
        String FieldSignKey = "RevokeMsgInfo_GroupUin";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.RevokeMsgInfo(),"a", String.class) :
                FindField(ClassTable.RevokeMsgInfo(),"c",String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field RevokeMsgInfo_IsTroop() {
        String FieldSignKey = "RevokeMsgInfo_IsTroop";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.RevokeMsgInfo(),"a", int.class) :
                FindField(ClassTable.RevokeMsgInfo(),"a",int.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field RevokeMsgInfo_OpUin() {
        String FieldSignKey = "RevokeMsgInfo_OpUin";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.RevokeMsgInfo(),"b", String.class) :
                FindField(ClassTable.RevokeMsgInfo(),"d",String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field RevokeMsgInfo_Sender() {
        String FieldSignKey = "RevokeMsgInfo_Sender";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.RevokeMsgInfo(),"d", String.class) :
                FindField(ClassTable.RevokeMsgInfo(),"h",String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field RevokeMsgInfo_shmsgseq() {
        String FieldSignKey = "RevokeMsgInfo_shmsgseq";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.RevokeMsgInfo(),"a", long.class) :
                FindField(ClassTable.RevokeMsgInfo(),"b",long.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field RevokeMsgInfo_msgUID() {
        String FieldSignKey = "RevokeMsgInfo_msgUID";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.RevokeMsgInfo(),"b", long.class) :
                FindField(ClassTable.RevokeMsgInfo(),"f",long.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field RevokeMsgInfo_msgTime() {
        String FieldSignKey = "RevokeMsgInfo_msgTime";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.RevokeMsgInfo(),"c", long.class) :
                FindField(ClassTable.RevokeMsgInfo(),"g",long.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field ForwardBaseOption_SelectActivity() {
        String FieldSignKey = "ForwardBaseOption_SelectActivity";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.ForwardBaseOption(),"a", Activity.class) :
                FindFirstField(ClassTable.ForwardBaseOption(),Activity.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field ForwardBaseOption_SelectActivityBundle() {
        String FieldSignKey = "ForwardBaseOption_SelectActivityBundle";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.ForwardBaseOption(),"a", Bundle.class) :
                FindFirstField(ClassTable.ForwardBaseOption(),Bundle.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field ForwardBaseOption_ResultList() {
        String FieldSignKey = "ForwardBaseOption_ResultList";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.ForwardBaseOption(),"b", ArrayList.class) :
                FindField(ClassTable.ForwardBaseOption(),"X",ArrayList.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field ChatSettingActivity_ASimpleItem() {
        String FieldSignKey = "ChatSettingActivity_ASimpleItem";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.ChatSettingActivity(),"a", ClassTable.FormSimpleItem()) :
                FindField(ClassTable.ChatSettingActivity(),"i",ClassTable.FormSimpleItem());
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }
    public static Field ColorNickText_Text() {
        String FieldSignKey = "ColorNickText_Text";
        if(FieldMap.containsKey(FieldSignKey))return FieldMap.get(FieldSignKey);
        Field f = NowQQVersion < 5670 ? FindField(ClassTable.ColorNickText(),"b", String.class) :
                FindField(ClassTable.ColorNickText(),"c", String.class);
        if(f == null)MLogCat.Print_Error("FieldLoader","Can't find fieldkey:"+FieldSignKey);
        if(f!=null) f.setAccessible(true);
        FieldMap.put(FieldSignKey,f);
        return f;
    }





    private static Field FindField(Class ObjClass,String FieldName,Class FieldType) {
        Class FindClass = ObjClass;
        while (FindClass !=null)
        {
            for (Field f : FindClass.getDeclaredFields())
            {
                if(f.getName().equals(FieldName) && f.getType().equals(FieldType))
                {
                    return f;
                }
            }
            FindClass = FindClass.getSuperclass();
        }
        return null;
    }
    public static Field FindFirstField(Class ObjClass,Class FieldType) {
        Class FindClass = ObjClass;
        while (FindClass !=null)
        {
            for (Field f : FindClass.getDeclaredFields())
            {
                if(f.getType().equals(FieldType))
                {
                    return f;
                }
            }
            FindClass = FindClass.getSuperclass();
        }
        return null;
    }
}
