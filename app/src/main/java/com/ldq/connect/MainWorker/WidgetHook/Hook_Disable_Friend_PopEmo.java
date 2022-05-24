package com.ldq.connect.MainWorker.WidgetHook;

import com.ldq.Utils.MClass;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Disable_Friend_PopEmo {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.popanim.util.PopOutEmoticonUtil"),
                "a", int.class,
                MClass.loadClass("com.tencent.mobileqq.emoticonview.EmoticonInfo"),int.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return false;
                    }
                }
        );
    }
}

