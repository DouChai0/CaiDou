package com.ldq.HookHelper.DexTable;

import com.ldq.Utils.MClass;

import java.util.HashMap;

public class ClassTable {
    public static final int NowQQVersion = VersionUtils.GetQQVersionInt();
    private static HashMap<String,Class> ClassMap = new HashMap<>(128);
    public static Class BaseChatPie() {
        String FindClassNameKey = "BaseChatPie";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class SessionInfo() {
        String FindClassNameKey = "SessionInfo";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.aio.SessionInfo");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class BaseSessionInfo() {
        String FindClassNameKey = "BaseSessionInfo";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class QQAppinterFace() {
        String FindClassNameKey = "QQAppInterface";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.app.QQAppInterface");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class RevokeHelper() {
        String FindClassNameKey = "RevokeHelper";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.aio.helper.AIORevokeMsgHelper");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class BaseChatItemLayout() {
        String FindClassNameKey = "BaseChatItemLayout";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.aio.BaseChatItemLayout");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class HelperProvider() {
        String FindClassNameKey = "HelperProvider";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.aio.helper.HelperProvider");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class ChatActivityFacade() {
        String FindClassNameKey = "ChatActivityFacade";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.ChatActivityFacade");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class TroopSettingActivity() {
        String FindClassNameKey = "TroopSettingActivity";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.troop.troopsetting.activity.TroopSettingActivity");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class TroopInfoData() {
        String FindClassNameKey = "TroopInfoData";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.troop.data.TroopInfoData");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class GdtMotiveVideoDialog() {
        String FindClassNameKey = "GdtMotiveVideoDialog";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.gdtad.basics.motivevideo.GdtMotiveVideoDialog");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class GdtMvViewController() {
        String FindClassNameKey = "GdtMvViewController";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.gdtad.basics.motivevideo.GdtMvViewController");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class GdtMotiveBrowsingDialog() {
        String FindClassNameKey = "GdtMotiveBrowsingDialog";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.gdtad.basics.motivebrowsing.GdtMotiveBrowsingDialog");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class MessageRecordFactory() {
        String FindClassNameKey = "MessageRecordFactory";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class MessageForMixedMsg() {
        String FindClassNameKey = "MessageForMixedMsg";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.data.MessageForMixedMsg");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class RevokeMsgInfo() {
        String FindClassNameKey = "RevokeMsgInfo";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.revokemsg.RevokeMsgInfo");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class QQMessageFacade() {
        String FindClassNameKey = "QQMessageFacade";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.imcore.message.QQMessageFacade");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class MessageRecord() {
        String FindClassNameKey = "MessageRecord";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.data.MessageRecord");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class ForwardRecentActivity() {
        String FindClassNameKey = "ForwardRecentActivity";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.ForwardRecentActivity");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class ForwardBaseOption() {
        String FindClassNameKey = "ForwardBaseOption";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.forward.ForwardBaseOption");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class ChatSettingActivity() {
        String FindClassNameKey = "ChatSettingActivity";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.ChatSettingActivity");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class FormSimpleItem() {
        String FindClassNameKey = "FormSimpleItem";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.widget.FormSimpleItem");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class QQSettingMe() {
        String FindClassNameKey = "QQSettingMe";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.activity.QQSettingMe");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }
    public static Class ColorNickText() {
        String FindClassNameKey = "ColorNickText";
        if(ClassMap.containsKey(FindClassNameKey))return ClassMap.get(FindClassNameKey);
        Class clz = MClass._loadClass("com.tencent.mobileqq.text.ColorNickText");
        ClassMap.put(FindClassNameKey,clz);
        return clz;
    }


}
