package com.ldq.connect.MainWorker.LittileHook;

import com.ldq.Utils.MClass;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Convert_BigEmo_To_Little {
    public static void Start() throws ClassNotFoundException {
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack"),
                    "parseMsgForAniSticker",
                    String.class, MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"), new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(MConfig.Get_Boolean("Main","MainSwitch","发送小表情",false))
                            {
                                String p = (String) param.args[0];
                                if(p.length()>2 && p.startsWith("Convert"))
                                {
                                    param.args[0] = p.substring(7);
                                }else
                                {
                                    param.args[0] = "";
                                }
                            }
                        }
                    }
            );
        }catch (Throwable th)
        {

        }

    }
}
