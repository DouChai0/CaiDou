package com.ldq.connect.MainWorker.WidgetHook;

import android.app.Activity;
import android.view.ViewGroup;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Hide_Main_App_Entry {
    public static void Start() throws Exception {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.config.business.MiniAppConfProcessor","a|b",boolean.class);
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽下拉小程序",false))
                    param.setResult(false);
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽下拉小程序",false))
                    param.setResult(false);
            }
        });

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.mini.api.impl.MiniAppServiceImpl"), "createMiniAppEntryManager",
                boolean.class, Activity.class, Object.class, Object.class, Object.class, Object.class, ViewGroup.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽下拉小程序",false))
                            param.setResult(null);
                    }
                }
        );

    }
}
