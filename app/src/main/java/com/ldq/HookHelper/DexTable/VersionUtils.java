package com.ldq.HookHelper.DexTable;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.ldq.connect.MHookEnvironment;

import de.robv.android.xposed.XposedBridge;

public class VersionUtils {
    public static int GetQQVersionInt()
    {
        try {
            //获取普通apk版本信息
            PackageManager pm = MHookEnvironment.MAppContext.getPackageManager();
            ApplicationInfo sAppInfo = pm.getApplicationInfo("com.tencent.mobileqq",PackageManager.GET_META_DATA);
            String UUID = sAppInfo.metaData.getString("com.tencent.rdm.uuid");

            return Integer.parseInt(UUID.substring(0,UUID.indexOf("_")));
        } catch (Throwable e) {
            XposedBridge.log(e);
            return 0;
        }
    }
}
