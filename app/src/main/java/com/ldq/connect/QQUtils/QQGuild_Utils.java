package com.ldq.connect.QQUtils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.StringUtils;
import com.ldq.connect.MHookEnvironment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class QQGuild_Utils {

    public static void Get_Guild_User_ClientID(String ChannelID,String TinyID){
        try{
            AtomicBoolean Ats = new AtomicBoolean();
            Object IGpsService = QQTools.getRuntimeService(MClass._loadClass("com.tencent.mobileqq.qqguildsdk.api.IGPSService"));
            Object mProxy = Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IGetUserInfoCallback")}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if(method.getName().equals("onGetUserInfo")){
                        Ats.getAndSet(true);
                    }

                    return null;
                }
            });
            ArrayList<String> UserList = new ArrayList<>();
            UserList.add(TinyID);

            new Handler(Looper.getMainLooper()).post(()->{
                try {
                    MMethod.CallMethod(IGpsService,"fetchUserInfo",void.class,new Class[]{
                            String.class, List.class,boolean.class,MClass._loadClass("com.tencent.mobileqq.qqguildsdk.callback.IGetUserInfoCallback")
                    },ChannelID,UserList,true,mProxy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            int i=0;

            while (!Ats.get()){
                i++;
                if(i>15)break;
                Thread.sleep(100);
            }

        }catch (Exception e) {
            MLogCat.Print_Debug(Log.getStackTraceString(e));
        }

    }


    public static String Get_Face_Url(String TinyID,String GuildID){
        try{
            Object IGpsService = QQTools.getRuntimeService(MClass._loadClass("com.tencent.mobileqq.qqguildsdk.api.IGPSService"));
            MMethod.CallMethod(IGpsService,"refreshGuildUserProfileInfo",void.class,new Class[]{String.class,String.class},
                    GuildID,TinyID);
            Get_Guild_User_ClientID(GuildID,TinyID);
            String face;
            try{
                face = MMethod.CallMethod(IGpsService,"getFullGuildUserUserAvatarUrl",String.class,
                        new Class[]{String.class,int.class},TinyID,2
                );
            }catch (Exception e){
                face = MMethod.CallMethod(IGpsService,"getFullGuildUserUserAvatarUrl",String.class,
                        new Class[]{String.class,String.class,int.class},GuildID,TinyID,2
                );
            }
            return face;
        }catch (Exception e){
            MLogCat.Print_Error("get_face_url",e);
            return "";
        }
    }
    public static String Get_Face_Url_(String TinyID,String GuildID){
        try{
            Object IGpsService = QQTools.getRuntimeService(MClass._loadClass("com.tencent.mobileqq.qqguildsdk.api.IGPSService"));

            String face = MMethod.CallMethod(IGpsService,"getFullGuildUserUserAvatarUrl",String.class,
                    new Class[]{String.class,String.class,int.class},GuildID,TinyID,2
            );
            return face;
        }catch (Exception e){
            MLogCat.Print_Error("get_face_url",e);
            return "";
        }
    }
    public static Object GetIGpsManager() throws Exception {
        return QQTools.getRuntimeService(MClass._loadClass("com.tencent.mobileqq.qqguildsdk.api.IGPSService"));
    }
    public static boolean Check_Face_Url_Same(String u1,String u2){
        try{
            if(u1.contains("thirdqq.qlogo.cn")){
                if(u2.contains("thirdqq.qlogo.cn")){
                    String[] Cut1 = StringUtils.GetStringMiddleMix(u1,"&k=","&");
                    String[] Cut2 = StringUtils.GetStringMiddleMix(u2,"&k=","&");
                    return Cut1[0].equals(Cut2[0]);
                }
            }

            if(u1.contains("qqchannel")){
                if(u2.contains("qqchannel")){
                    int ind = u1.indexOf("com/");
                    int index2 = u2.indexOf("com/");

                    return u1.substring(ind,ind+24).equals(u2.substring(index2,index2+24));
                }
            }
            return false;

        }catch (Exception e){
            return false;
        }
    }
    public static String Get_Guild_Creator(String GuildID){
        ArrayList<GuildUtils.GuildInfo> infos = GuildUtils.GetGuildList();
        for(GuildUtils.GuildInfo mInfo : infos){
            if(GuildID.equals(mInfo.GuildID)){
                return mInfo.Creator;
            }
        }
        return "";
    }
}
