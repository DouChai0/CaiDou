package com.ldq.connect.MainWorker.WidgetHook;

import android.view.View;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Troop_Slide_App {
    public static void Start() throws Exception {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.drawer.TroopAppShortcutDrawer","a|b", View.class);
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","隐藏侧滑",false))param.setResult(null);
            }
        });

        XposedHelpers.findAndHookConstructor(MClass.loadClass("com.tencent.mobileqq.activity.aio.drawer.TroopAppShortcutDrawer"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        if(MConfig.Get_Boolean("Main","MainSwitch","替换侧滑",false))
                        {
                            Object obj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.activity.aio.drawer.TroopMultiCardDrawer"),new Class[]{
                                    MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie")
                            },param.args);
                            param.setResult(obj);
                        }
                    }
                }
        );




    }
}
