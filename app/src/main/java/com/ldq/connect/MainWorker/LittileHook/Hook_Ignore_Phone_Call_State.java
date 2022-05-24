package com.ldq.connect.MainWorker.LittileHook;

import android.content.Context;

import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Ignore_Phone_Call_State {
    public static void Start()
    {
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.kandian.glue.video.VideoVolumeControl", MHookEnvironment.mLoader, "isInPhoneCall", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","忽略通话状态",false)) {
                    param.setResult(false);
                }
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.app.QQAppInterface", MHookEnvironment.mLoader, "isVideoChatting", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","忽略通话状态",false))
                {
                    param.setResult(false);
                }
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.av.camera.QavCameraUsage", MHookEnvironment.mLoader, "a",
                Context.class,boolean.class,
                new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","忽略通话状态",false))
                {
                    param.setResult(false);
                }
            }
        });
    }


}
