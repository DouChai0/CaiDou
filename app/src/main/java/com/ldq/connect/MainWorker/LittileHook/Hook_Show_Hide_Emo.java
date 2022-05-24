package com.ldq.connect.MainWorker.LittileHook;

import com.ldq.Utils.MClass;
import com.ldq.connect.QQUtils.QQTools;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Show_Hide_Emo {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.emoticon.QQSysAndEmojiResInfo"), "isEmoticonHide",
                MClass.loadClass("com.tencent.mobileqq.emoticon.QQSysAndEmojiResInfo$QQEmoConfigItem"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.setResult(false);
                    }
                }
        );
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.emoticon.QQSysFaceUtil"), "isAniSticker",
                    int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            int Check = (int) param.args[0];
                            param.setResult(QQTools.IsSticker(Check));
                        }
                    }
            );
        }catch (Throwable e)
        {

        }

    }
}
