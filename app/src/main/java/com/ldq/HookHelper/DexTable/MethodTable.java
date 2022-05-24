package com.ldq.HookHelper.DexTable;

import android.view.ViewGroup;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MethodTable {
    private static final int NowQQVersion = VersionUtils.GetQQVersionInt();
    private static final HashMap<String, Method> MethodMap = new HashMap<>(128);
    public static Method BaseChatPie_InitData() {
        String MethodNameKey = "BaseChatPie_InitData";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m;
        if (NowQQVersion > 6440) {
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"s", void.class,new Class[0]);
        }
        else if (NowQQVersion > 5870) {
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"r", void.class,new Class[0]);
        }
        else if(NowQQVersion > 5570) {
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"q", void.class,new Class[0]);
        }
        else {
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"f", void.class,new Class[0]);
        }

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }
        MethodMap.put(MethodNameKey,m);
        return m;
    }
    public static Method BaseChatPie_GetAIORootViewGroup() {
        String MethodNameKey = "BaseChatPie_GetAIORootViewGroup";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m;
        if(NowQQVersion>6440){
            m = MMethod.FindFirstMethod(ClassTable.BaseChatPie(),ViewGroup.class,new Class[0]);
        }
        else if(NowQQVersion > 6200){
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"aZ", ViewGroup.class,new Class[0]);
        }
        else if(NowQQVersion > 5870){
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"aY", ViewGroup.class,new Class[0]);
        }
        else if(NowQQVersion > 5675) {
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"aW", ViewGroup.class,new Class[0]);
        }
        else if(NowQQVersion > 5570) {
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"aV", ViewGroup.class,new Class[0]);
        }else {
            m = MMethod._FindMethod(ClassTable.BaseChatPie(),"a", ViewGroup.class,new Class[0]);
        }

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }else{
            m.setAccessible(true);
        }
        MethodMap.put(MethodNameKey,m);
        return m;
    }
    public static Method ChatActivityFacade_SendPttByPath() {
        String MethodNameKey = "ChatActivityFacade_SendPttByPath";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m =
                NowQQVersion < 5670 ?
                MMethod._FindMethod(ClassTable.ChatActivityFacade(),"a", long.class,new Class[]{ClassTable.QQAppinterFace(),ClassTable.SessionInfo(),String.class}):
                MMethod._FindMethod(ClassTable.ChatActivityFacade(),"d", long.class,new Class[]{ClassTable.QQAppinterFace(),ClassTable.SessionInfo(),String.class});

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }else{
            m.setAccessible(true);
        }

        MethodMap.put(MethodNameKey,m);
        return m;
    }
    public static Method GdtMvViewController_InitAds() {
        String MethodNameKey = "GdtMvViewController_InitAds";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m =
                NowQQVersion < 5670 ?
                        MMethod._FindMethod(ClassTable.GdtMvViewController(),"a",boolean.class,new Class[]{boolean.class}):
                        MMethod._FindMethod(ClassTable.GdtMvViewController(),"c",boolean.class,new Class[]{boolean.class});

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }else{
            m.setAccessible(true);
        }

        MethodMap.put(MethodNameKey,m);
        return m;
    }
    public static Method MessageRecordFactory_BuildMixedMsg() {
        String MethodNameKey = "MessageRecordFactory_BuildMixedMsg";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m =
                NowQQVersion < 5670 ?
                        MMethod._FindMethod(ClassTable.MessageRecordFactory(),"a",ClassTable.MessageForMixedMsg(),new Class[]{ClassTable.QQAppinterFace(),String.class,String.class,int.class}):
                        MMethod._FindMethod(ClassTable.MessageRecordFactory(),"g",ClassTable.MessageForMixedMsg(),new Class[]{ClassTable.QQAppinterFace(),String.class,String.class,int.class});
        if (m == null){
            m =  MMethod._FindMethod(ClassTable.MessageRecordFactory(),"h",ClassTable.MessageForMixedMsg(),new Class[]{ClassTable.QQAppinterFace(),String.class,String.class,int.class});
        }

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }else{
            m.setAccessible(true);
        }
        MethodMap.put(MethodNameKey,m);
        return m;
    }
    public static Method MessageFacade_RevokeMessage() {
        String MethodNameKey = "MessageFacade_RevokeMessage";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m =
                NowQQVersion < 5670 ?
                        MMethod._FindMethod(ClassTable.QQMessageFacade(),"d",void.class,new Class[]{ClassTable.MessageRecord()}):
                        MMethod._FindMethod(ClassTable.QQMessageFacade(),"f",void.class,new Class[]{ClassTable.MessageRecord()});

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }else{
            m.setAccessible(true);
        }
        MethodMap.put(MethodNameKey,m);
        return m;
    }
    public static Method ForwardBaseOption_ShowConfirmDialog() {
        String MethodNameKey = "ForwardBaseOption_ShowConfirmDialog";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m =
                NowQQVersion < 5670 ?
                        MMethod._FindMethod(ClassTable.ForwardBaseOption(),"o",void.class,new Class[0]):
                        NowQQVersion < 6155 ?
                        MMethod._FindMethod(ClassTable.ForwardBaseOption(),"I",void.class,new Class[0]):
                        MMethod._FindMethod(ClassTable.ForwardBaseOption(),"J",void.class,new Class[0]);

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }else{
            m.setAccessible(true);
        }
        MethodMap.put(MethodNameKey,m);
        return m;
    }
    public static Method QQSettingMe_InitVipInfoAd() {
        String MethodNameKey = "QQSettingMe_InitVipInfoAd";
        if(MethodMap.containsKey(MethodNameKey))return MethodMap.get(MethodNameKey);
        Method m =
                NowQQVersion < 5670 ?
                        MMethod._FindMethod(ClassTable.QQSettingMe(),"g",void.class,new Class[0]):
                        MMethod._FindMethod(ClassTable.QQSettingMe(),"j",void.class,new Class[0]);

        if(m == null)
        {
            MLogCat.Print_Error("MethodInit","MethodLoader return null, MethodFindKey is:" + MethodNameKey);
        }else{
            m.setAccessible(true);
        }
        MethodMap.put(MethodNameKey,m);
        return m;
    }



}
