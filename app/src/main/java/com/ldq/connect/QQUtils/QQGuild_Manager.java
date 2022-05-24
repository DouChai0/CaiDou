package com.ldq.connect.QQUtils;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.MHookEnvironment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class QQGuild_Manager {
    public static void Guild_Forbidden(String ChannelID,String UserTinyID,long Time){
        try{
            Object IGpsManager = QQGuild_Utils.GetIGpsManager();
            Object ResultProxy = Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return null;
                }
            });
            MMethod.CallMethod(IGpsManager,"setMemberShutUp",void.class,new Class[]{
                    String.class,String.class,long.class, MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")
            },ChannelID,UserTinyID,QQTools.GetServerTime()+Time,ResultProxy);
        }catch (Exception e){
            MLogCat.Print_Error("Guild_Forbidden_All",e);
        }
    }
    public static void Guild_ForbiddenAll(String ChannelID,long Time){
        try{
            Object IGpsManager = QQGuild_Utils.GetIGpsManager();
            Object ResultProxy = Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return null;
                }
            });
            MMethod.CallMethod(IGpsManager,"setGuildShutUp",void.class,new Class[]{
                    String.class,long.class, MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")
            },ChannelID,Time,ResultProxy);
        }catch (Exception e){
            MLogCat.Print_Error("Guild_Forbidden_All",e);
        }
    }
    public static void Guild_Kick_User(String ChannelID,String UserTinyID,boolean IsBlack){
        try{
            Object IGpsManager = QQGuild_Utils.GetIGpsManager();
            Object ResultProxy = Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return null;
                }
            });
            ArrayList<String> list = new ArrayList<>();
            list.add(UserTinyID);
            MMethod.CallMethod(IGpsManager,"kickGuildUsers",void.class,new Class[]{
                    String.class, List.class,boolean.class, MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")
            },ChannelID,list,IsBlack,ResultProxy);
        }catch (Exception e){
            MLogCat.Print_Error("Guild_Forbidden_All",e);
        }
    }
    public static void Guild_Revoke(Object Message){
        try{
            Object RevokeHelper = QQTools.getBusinessHandler("com.tencent.mobileqq.guild.message.api.impl.GuildRevokeMessageHandler");
            MMethod.CallMethod(RevokeHelper,"a",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")},Message);
        }catch (Exception e){
            MLogCat.Print_Error("Guild_revoke",e);
        }

    }

}
