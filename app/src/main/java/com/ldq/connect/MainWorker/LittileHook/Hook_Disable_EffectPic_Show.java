package com.ldq.connect.MainWorker.LittileHook;

import android.graphics.Bitmap;

import com.ldq.Utils.MClass;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Disable_EffectPic_Show {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.trooppiceffects.TroopPicEffectsController"), "a",
                int.class, Bitmap.class, int.class, MClass.loadClass("com.tencent.mobileqq.trooppiceffects.TroopPicEffectsController$OnAnimationEndListener"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽秀图",false))
                        {
                            param.setResult(null);
                        }
                    }
                }
        );
    }
}
