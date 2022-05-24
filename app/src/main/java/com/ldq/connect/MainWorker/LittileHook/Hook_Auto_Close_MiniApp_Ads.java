package com.ldq.connect.MainWorker.LittileHook;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.HookHelper.DexTable.MethodTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Auto_Close_MiniApp_Ads {
    public static void Start() throws Exception {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.gdtad.basics.motivebrowsing.GdtMotiveBrowsingDialog"), "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if(!MConfig.Get_Boolean("Main","MainSwitch","跳过广告",false))return;
                Object thiss = param.thisObject;
                new Handler(Looper.getMainLooper()).postDelayed(()->{
                    try {
                        FieldTable.GdtMotiveBrowsingDialog_IsWatchedAds().set(thiss,true);
                        MMethod.CallMethod(thiss,"a",void.class,new Class[0]);
                    } catch (Exception e) {
                        MLogCat.Print_Error("Skip Video Error2",e);
                    }

                },200);
            }
        });

        Method m = MethodTable.GdtMvViewController_InitAds();
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if(!MConfig.Get_Boolean("Main","MainSwitch","跳过广告",false))return;
                FieldTable.GdtMvViewController_IsWatchedAds().set(param.thisObject,true);
            }
        });

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.gdtad.basics.motivevideo.GdtMotiveVideoDialog"), "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if(!MConfig.Get_Boolean("Main","MainSwitch","跳过广告",false))return;
                Object sObj = FieldTable.GdtMotiveVideoDialog_MyController().get(param.thisObject);
                new Handler(Looper.getMainLooper()).postDelayed(()->{
                    try {
                        MMethod.CallMethod(sObj,"a",void.class,new Class[]{boolean.class},true);
                        MMethod.CallMethod(sObj,"b",void.class,new Class[]{boolean.class},true);
                        MMethod.CallMethod(sObj,"a",void.class,new Class[0]);
                    } catch (Exception e) {
                        MLogCat.Print_Error("Skip Video Error1",e);
                    }

                },1200);

            }
        });
    }
}
