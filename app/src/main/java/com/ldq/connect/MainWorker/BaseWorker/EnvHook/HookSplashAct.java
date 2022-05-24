package com.ldq.connect.MainWorker.BaseWorker.EnvHook;

import android.app.Activity;
import android.os.Bundle;

import com.ldq.Utils.MClass;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookSplashAct {
    static XC_MethodHook.Unhook unhook = null;
    public static Activity FirstAct;
    public static void StartHook(Runnable run){
        unhook = XposedHelpers.findAndHookMethod(MClass._loadClass("com.tencent.mobileqq.activity.SplashActivity"),
                "doOnCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        unhook.unhook();
                        FirstAct = (Activity) param.thisObject;
                        run.run();
                    }
                });
    }
}
