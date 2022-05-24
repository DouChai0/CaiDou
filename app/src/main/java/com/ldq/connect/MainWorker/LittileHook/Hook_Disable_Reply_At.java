package com.ldq.connect.MainWorker.LittileHook;

import com.ldq.Utils.MClass;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Disable_Reply_At {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.rebuild.input.InputUIUtils"), "a",
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.AIOContext"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        //MLogCat.Print_Debug(""+param.args[2]);
                        if(MConfig.Get_Boolean("Main","MainSwitch","去除艾特",false))
                        {
                            if(!(boolean)param.args[2])param.setResult(null);
                        }
                    }
                }
        );
    }
}
