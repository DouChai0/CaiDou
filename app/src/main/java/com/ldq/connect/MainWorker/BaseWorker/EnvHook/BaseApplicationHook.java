package com.ldq.connect.MainWorker.BaseWorker.EnvHook;

import com.ldq.Utils.MClass;
import com.ldq.connect.MHookEnvironment;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class BaseApplicationHook {
    public static void start() throws ClassNotFoundException {
        //在QQ切换账号的时候会调用
        XposedHelpers.findAndHookConstructor(MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                MClass.loadClass("com.tencent.common.app.BaseApplicationImpl"), String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        MHookEnvironment.AppInterface = param.thisObject;
                    }
                });
    }
}
