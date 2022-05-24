package com.ldq.connect.QQUtils;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MixUtils;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.UUID;

public class BaseInfo {
    public static Object GetAppInterface() throws Exception {
        Object sApplication = MMethod.CallMethod(null, MClass.loadClass("com.tencent.common.app.BaseApplicationImpl"),"getApplication",
                MClass.loadClass("com.tencent.common.app.BaseApplicationImpl"),new Class[0],new Object[0]
        );
        Object AppRuntime = MMethod.CallMethod(sApplication,sApplication.getClass(),"getRuntime",MClass.loadClass("mqq.app.AppRuntime"),new Class[0]);
        return AppRuntime;
    }
    public static String CacheUin="";
    public static Object CacheInter;
    public static int GetSelfLevel(){
        try{
            Method mGetRuntime = null;
            for(Method ms : MClass.loadClass("mqq.app.AppRuntime").getDeclaredMethods()){
                if(ms.getName().equals("getRuntimeService"))
                {
                    mGetRuntime = ms;
                    break;
                }
            }
            String CurrentUin = GetCurrentUin_Direct();
            Object FriendManagerImpl = mGetRuntime.invoke(GetAppInterface(),MClass.loadClass("com.tencent.mobileqq.profilecard.api.IProfileDataService"),"all");
            Object MCard = MMethod.CallMethod(FriendManagerImpl,"getProfileCard",MClass.loadClass("com.tencent.mobileqq.data.Card"),new Class[]{
                    String.class,boolean.class
            },CurrentUin,false);
            return MField.GetField(MCard,"iQQLevel",int.class);

        }catch (Exception e){
            return -1;
        }

    }
    public static String getSelfName(){
        try{
            Object AppInterfact =GetAppInterface();
            return MMethod.CallMethod(AppInterfact,"getCurrentNickname",String.class,new Class[0]);
        }catch (Exception e){
            return "";
        }

    }
    public static boolean IsCurrentEffect()
    {

        try{
            Object AppRuntime = GetAppInterface();
            return MMethod.CallMethod(AppRuntime,AppRuntime.getClass(),"isLogin",boolean.class,new Class[0],new Object[0]);
        }catch (Exception ex)
        {
            return false;
        }
    }
    public static String GetCurrentUin_Direct()
    {
        try {
            Object AppRuntime = GetAppInterface();
            String mUin = MMethod.CallMethod(AppRuntime,AppRuntime.getClass(),"getCurrentAccountUin",String.class,new Class[0],new Object[0]);
            return mUin;
        } catch (Exception exception) {
            return "";
        }

    }
    public static String GetCurrentUin()
    {
        try{
            if(!TextUtils.isEmpty(CacheUin))
            {
                if(MHookEnvironment.AppInterface!=null)
                {
                    if(MHookEnvironment.AppInterface==CacheInter)return CacheUin;
                    CacheInter = MHookEnvironment.AppInterface;
                    Object Interface = MHookEnvironment.AppInterface;
                    String mUin = MMethod.CallMethod(Interface,Interface.getClass(),"getCurrentAccountUin",String.class,new Class[0],new Object[0]);
                    CacheUin = mUin;
                    return mUin;
                }
            }
            Object Interface = GetAppInterface();
            String mUin = MMethod.CallMethod(Interface,Interface.getClass(),"getCurrentAccountUin",String.class,new Class[0],new Object[0]);
            CacheUin = mUin;
            return mUin;
        }
        catch (Exception e)
        {
            MLogCat.Print_Error("GetCurrentUin", Log.getStackTraceString(e));
            return "";
        }
    }
    public static String GetCurrentUinO2(){
        String Uin = GetCurrentUin();
        while (Uin.length()<10)
        {
            Uin = "0"+Uin;
        }
        return "o"+Uin;
    }
    public static String GetCurrentGroupUin()  {
        try {
            int SessionType = (int) FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
            String mStr = "";
            if(SessionType==1)
            {
                mStr = (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
            }
            else if (SessionType==0)
            {
               mStr = (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
            }
            else if(SessionType == 1000)
            {
               mStr = (String) FieldTable.SessionInfo_InTroopUin().get(MHookEnvironment.CurrentSession);
            }else if (SessionType == 10014){
                mStr = FieldTable.SessionInfo_ChannelID().get(MHookEnvironment.CurrentSession) + "&" +
                        FieldTable.SessionInfo_ChannelCharID().get(MHookEnvironment.CurrentSession);
            }
            return mStr;
        } catch (Exception e) {
            return "";
        }
    }
    public static boolean IsCurrentGroup() {

        try {
            Integer mInt =(int) FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
            return mInt.intValue()==1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean IsCurrentChannel() {

        try {
            Integer mInt =(int) FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
            return mInt.intValue()==10014;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String GetCurrentDevID()
    {
        String SerialID = Settings.Secure.ANDROID_ID;
        String MyUUID = GetCurrentUUID();
        String DevIDHash = SerialID.hashCode()+""+MyUUID.hashCode();
        return DevIDHash;
    }

    public static String GetCurrentUUID()
    {
        File f = new File(MHookEnvironment.AppPath+"/app_"+ MHookEnvironment.RndToken+"/"+ MixUtils.MixName("UID"));
        if(!f.exists())
        {
            UUID sNew = UUID.randomUUID();
            FileUtils.WriteFileByte(f.getAbsolutePath(),sNew.toString().getBytes());
        }
        byte[] sUUID = FileUtils.ReadFileByte(f.getAbsolutePath());
        if(sUUID==null)return ""+Math.random();
        return new String(sUUID);
    }
    public static String GetCurrentTinyID(){
        try {
            Object IGpsService = QQTools.getRuntimeService(MClass._loadClass("com.tencent.mobileqq.qqguildsdk.api.IGPSService"));
            String TinyID = MMethod.CallMethod(IGpsService,"getSelfTinyId",String.class,new Class[0]);
            return TinyID;
        } catch (Exception e) {
            return "";
        }

    }
}
