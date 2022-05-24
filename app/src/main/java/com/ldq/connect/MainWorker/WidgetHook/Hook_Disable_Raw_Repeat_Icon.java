package com.ldq.connect.MainWorker.WidgetHook;

import android.content.Context;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Disable_Raw_Repeat_Icon {
    public static void Start() throws Exception {
        Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ChatMessage", "isFollowMessage", boolean.class);
        XposedBridge.hookMethod(med, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (MConfig.Get_Boolean("Main","MainSwitch","消息复读",false))
                {
                    param.setResult(false);
                }
            }
        });
        med = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.MarketFaceItemBuilder", "c", void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.item.MarketFaceItemBuilder$Holder")
        });
        XposedBridge.hookMethod(med, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (MConfig.Get_Boolean("Main","MainSwitch","消息复读",false))
                {
                    MField.SetField(param.args[0],"isFlowMessage",false);
                }
            }
        });

        med = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.TextItemBuilder", "a", void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"),
                Context.class,
                MClass.loadClass("com.tencent.mobileqq.activity.aio.item.TextItemBuilder$Holder")

        });
        XposedBridge.hookMethod(med, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                MField.SetField(param.args[0],"isFlowMessage",false);
            }
        });


    }
}
