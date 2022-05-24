package com.ldq.connect.MainWorker.WidgetHook;

import android.view.View;

import com.ldq.Utils.MClass;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Disable_Other_Font {
     public static void Start() throws ClassNotFoundException {
         XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.TextItemBuilder"), "b",
                 View.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                 new XC_MethodHook() {
                     @Override
                     protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                         super.beforeHookedMethod(param);
                         if(MConfig.Get_Boolean("Main","MainSwitch","全局屏蔽字体",false))
                         {
                             param.setResult(null);
                         }
                     }
                 }
         );

         XposedHelpers.findAndHookMethod(MClass.loadClass("com.etrump.mixlayout.ETTextView"), "setFont",
                 MClass.loadClass("com.etrump.mixlayout.ETFont"), long.class,int.class,
                 new XC_MethodHook() {
                     @Override
                     protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                         super.beforeHookedMethod(param);
                         if(MConfig.Get_Boolean("Main","MainSwitch","全局屏蔽字体",false))
                         {
                             param.setResult(null);
                         }
                     }
                 }
         );
     }
}
