package com.ldq.connect.MainWorker.LittileHook;


import com.ldq.connect.MHookEnvironment;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Allow_Scan_Pic_QRCode {

    public static void Start()
    {
        XposedHelpers.findAndHookMethod("com.tencent.open.agent.QrAgentLoginManager$2", MHookEnvironment.mLoader, "a",
                boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[0] = false;
            }
        });
    }

}
