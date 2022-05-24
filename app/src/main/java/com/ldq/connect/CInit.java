package com.ldq.connect;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.ConfigCleaner;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.StartBaseHook;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.StartDelayHook;
import com.ldq.connect.ServerTool.UserStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CInit implements IXposedHookLoadPackage {
    public static void InitDexEntry(ClassLoader loader, String QQProcessName, Context AppContext,String RndToken)
    {
        try{
            MHookEnvironment.mLoader = loader;
            MHookEnvironment.MAppContext = AppContext;
            MHookEnvironment.RndToken = RndToken;
            MHookEnvironment.AppPath = MHookEnvironment.MAppContext.getApplicationInfo().dataDir;
            MHookEnvironment.ProcessName = QQProcessName;

            if(QQProcessName.endsWith(":MSF") || QQProcessName.endsWith(":peak")){
                return;
            }

            if(QQProcessName.equals("com.tencent.mobileqq")){

                if(!PathConfigSet.CheckForConfigPath(true)){
                    return;
                }
            }else {
                if(!PathConfigSet.CheckForConfigPath(false)){
                    return;
                }
            }
            ConfigCleaner.StartClean();


            if(UserStatus.CheckLocalBlackFlag())return;

            InitForEnvironment.StartInit();

            if(QQProcessName.equals("com.tencent.mobileqq")){
                FileUtils.deleteFile(new File(MHookEnvironment.PublicStorageModulePath + "Cache/"));
            }

            if(QQProcessName.equals("com.tencent.mobileqq"))
            {

                StartBaseHook.MainHook();
                StartDelayHook.DelayHook();
            }else{
                StartBaseHook.ExtraHook();
            }

        }catch (Throwable th)
        {
            Utils.ShowToast("An error occurs in module init process.\n"+Log.getStackTraceString(th));
            XposedBridge.log(th);
        }


    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        MHookEnvironment.AppPath = lpparam.appInfo.dataDir;
        MHookEnvironment.mLoader = lpparam.classLoader;
        MHookEnvironment.ProcessName = lpparam.processName;
        MHookEnvironment.RndToken = "Token";
        HookInitContext();
    }
    private static void HookInitContext(){
        XposedHelpers.findAndHookMethod(MClass._loadClass("com.tencent.mobileqq.qfix.QFixApplication"), "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                MHookEnvironment.MAppContext = (Context) param.args[0];
                MHookEnvironment.AppPath = MHookEnvironment.MAppContext.getApplicationInfo().dataDir;
                InitPackVersion();

                InitDexEntry(MHookEnvironment.mLoader,MHookEnvironment.ProcessName,MHookEnvironment.MAppContext,MHookEnvironment.RndToken);
            }
        });
    }
    private static void InitPackVersion()
    {
        try {
            //获取普通apk版本信息
            PackageManager pm = MHookEnvironment.MAppContext.getPackageManager();
            PackageInfo mSelfInfo = pm.getPackageInfo("com.tencent.mobileqq",0);
            String Version_1 = mSelfInfo.versionName;

            //获取QQ小版本信息
            ApplicationInfo sAppInfo = pm.getApplicationInfo("com.tencent.mobileqq",PackageManager.GET_META_DATA);
            String UUID = sAppInfo.metaData.getString("com.tencent.rdm.uuid");
            try{
                MHookEnvironment.MVersion = Version_1+"."+UUID.substring(0,UUID.indexOf("_"));
                MHookEnvironment.MVersionInt = Integer.parseInt(UUID.substring(0,UUID.indexOf("_")));
            }catch (Exception e){
                MHookEnvironment.MVersion = Version_1;
                MHookEnvironment.MVersionInt = 0;
            }

        } catch (Throwable e) {
            XposedBridge.log(e);
            Toast.makeText(MHookEnvironment.MAppContext, "小菜豆->无法获取QQ版本信息,请不要对QQ隐藏QQ自己,也请不要使用被深度修改的美化版QQ,如果未隐藏也发生此错误,请进行反馈:\n"+e, Toast.LENGTH_LONG).show();
        }
    }
}
