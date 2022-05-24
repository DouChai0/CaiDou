package com.ldq.connect.ServerTool;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.SMToolHelper;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.HookConfig.MixUtils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;

import java.io.File;
import java.util.UUID;

public class UserStatus {
    public static final int Permission_Black = 1;
    public static final int Permission_Donate = 1<<1;
    public static final int Permission_Old_Donate = 1<<2;
    public static final int Permission_Change_Info = 1<<3;
    public static final int PERMISSION_TEST = 1<<4;
    public static final int PERMISSION_SPRING = 1 << 5;
    private static int UserStatusFlags = 0;

    public static String GetStatus(){
        String t = "QQ:"+BaseInfo.GetCurrentUin();
        t+= "\nDev_Hash:"+GetCurrentDevID().hashCode();
        t+="\nstatus:0x"+Integer.toHexString(UserStatusFlags);
        return t;
    }
    public static boolean CheckLocalBlackFlag()
    {
        return GlobalConfig.Get_Boolean("black",false);
    }
    public static boolean CheckIsTester()
    {
        return (UserStatusFlags & PERMISSION_TEST)>0;
    }

    public static boolean CheckIsDonator()
    {
        return true;
    }
    public static void StartUpdataPermissionInfo()
    {
        try {
            Thread.sleep(5000);
            String Ret = SMToolHelper.GetDataFromServer(EncEnv.Get_Dev_Info,GetCurrentDevID());
            UserStatusFlags = Integer.parseInt(Ret);

            SaveBlackInfo(UserStatusFlags);

        } catch (Exception e) {

        }

    }
    public static void SaveBlackInfo(int CheckFlag) {
        if(BaseInfo.GetCurrentUin_Direct().equals("1071791"))return;
        if((CheckFlag&Permission_Black)>0) {
            Utils.ShowToast("当前账号已被拉黑,请 停用 模块后再使用QQ");
            GlobalConfig.Put_Boolean("black",true);
            new Handler(Looper.getMainLooper()).postDelayed(()->System.exit(-1),3000);
        }
    }
    public static String GetCurrentDevID()
    {
        String SerialID = Settings.Secure.ANDROID_ID;
        String MyUUID = GetCurrentUUID();
        String DevIDHash = SerialID.hashCode()+""+MyUUID.hashCode();
        return DevIDHash;
    }
    private static String GetCurrentUUID()
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
}
