package com.ldq.connect.MainWorker.WidgetHook;

import android.text.Spanned;

import com.ldq.Utils.MClass;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Unlock_Input_Length_Limit {
    static boolean Available_UnlockInput = true;
    public static void Start() throws ClassNotFoundException {
        try {
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.FullScreenInputHelper$4"), "filter",
                    CharSequence.class, int.class, int.class, Spanned.class, int.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            //if (BaseConfig.GetBoolean("解除输入限制", false))
                            param.setResult(null);
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.rebuild.input.send.MaxLengthSendMsgCallback"), "beforeMessageSend",
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.core.AIOContext"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.core.input.SendLogicParam")
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            //if (BaseConfig.GetBoolean("解除输入限制", false))
                            param.setResult(false);
                        }
                    }
            );
        } catch (Throwable th)
        {
            Available_UnlockInput = false;
        }

    }
}
