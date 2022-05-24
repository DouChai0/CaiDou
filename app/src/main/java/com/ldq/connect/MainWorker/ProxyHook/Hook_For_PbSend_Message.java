package com.ldq.connect.MainWorker.ProxyHook;

import com.ldq.Utils.MClass;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Avoid_Some_Message;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_PbSend_Message {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.app.MessageHandler"), "a",
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver"),
                boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object obj = param.args[0];
                        Handler_Avoid_Some_Message._Handle_VoiceTime(obj);
                    }
                }
        );
    }
}
