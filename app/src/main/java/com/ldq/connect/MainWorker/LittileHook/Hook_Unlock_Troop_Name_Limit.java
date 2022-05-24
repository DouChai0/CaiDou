package com.ldq.connect.MainWorker.LittileHook;

import android.text.Spanned;

import com.ldq.Utils.MClass;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Unlock_Troop_Name_Limit
{
    public static void Start() throws Exception {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.editservice.EditTroopMemberNickService$EmojiFilter"), "filter",
                CharSequence.class, int.class, int.class, Spanned.class, int.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","解除表情限制",false))param.setResult(null);
                    }
                }

        );
    }
}
