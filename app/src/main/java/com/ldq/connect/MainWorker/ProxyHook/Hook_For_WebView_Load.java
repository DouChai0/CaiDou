package com.ldq.connect.MainWorker.ProxyHook;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_OneStrokeHB_Decode;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_SendStrokeHB;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_WebView_Load {
    static Activity availableAct = null;
    static Object CustomWebView;
    static Object HostWebView;
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.biz.pubaccount.CustomWebView"), "loadUrl", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                    String Url = (String) param.args[0];

                    CustomWebView = param.thisObject;
                    new Handler(Looper.getMainLooper()).postDelayed(()->{
                        if(MConfig.Get_Boolean("Main","MainSwitch","一笔画红包助手",false)){
                            if(Url.contains("oneStrokePaintHb/index?"))
                            {
                                DecodeHostWebview();
                                Handler_OneStrokeHB_Decode.InitAvailable(Url);
                                Handler_OneStrokeHB_Decode.ActivityAvailable(availableAct);
                            }
                            if (Url.contains("sendRedpack/index?")){
                                DecodeHostWebview();
                                Handler_SendStrokeHB.InitAct(availableAct);
                            }
                        }

                    },2000);


            }
        });

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQBrowserActivity"), "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                availableAct = (Activity) param.thisObject;
            }
        });


        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.biz.pubaccount.CustomWebView"), "onDetachedFromWindow", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Handler_OneStrokeHB_Decode.DestroyFloatWindow();
                availableAct = null;
            }
        });
    }
    public static void JSBridge_LoadURL(String data) throws Exception {
        if(HostWebView != null){
            new Handler(Looper.getMainLooper())
                    .post(()-> {
                        try {
                            MMethod.CallMethod(HostWebView,"loadUrl",void.class,new Class[]{String.class},data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

        }
    }
    private static void DecodeHostWebview(){
        try {
            Object Host = MMethod.CallMethod(availableAct,"getHostWebView",
                    MClass.loadClass("com.tencent.smtt.sdk.WebView"),new Class[0]);
            HostWebView = Host;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
