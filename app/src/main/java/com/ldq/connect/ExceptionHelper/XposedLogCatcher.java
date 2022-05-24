package com.ldq.connect.ExceptionHelper;

import android.util.Log;

import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class XposedLogCatcher {
    public static void StartCatcher(){
        Method med;
        Method medErr;
        try {

            med = XposedBridge.class.getMethod("log", String.class);
            medErr = XposedBridge.class.getMethod("log", Throwable.class);
        } catch (NoSuchMethodException e) {
            StringPool_XpLog.Add("No Xposed Log");
            StringPool_XpErr.Add("No Xposed Log");
            return;
        }
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String sLine = (String) param.args[0];
                if (sLine == null) sLine = "null";
                String StartLine = "("+ Utils.GetNowTime()+")"
                        +"["+MHookEnvironment.ProcessName+"->"+Thread.currentThread().getName()+"] "
                        +sLine;
                StringPool_XpLog.Add(StartLine);
            }
        };

        XC_MethodHook hookErr = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Throwable sLine = (Throwable) param.args[0];
                if (sLine == null)return;
                String StartLine = "("+ Utils.GetNowTime()+")"
                        +"["+MHookEnvironment.ProcessName+"->"+Thread.currentThread().getName()+"]\n"
                        + Log.getStackTraceString(sLine);
                StringPool_XpErr.Add(StartLine);
            }
        };

        try{

            XposedBridge.hookMethod(med,hook);
            XposedBridge.hookMethod(medErr,hookErr);
            //XposedBridge.log("小菜豆->Xposed_LogCatcher_InitSuccess");
        }catch (Throwable e){
            try{
                ByPassInnerHook.StartByPass();
                XposedBridge.hookMethod(med,hook);
                ByPassInnerHook.EndBypass();

                ByPassInnerHook.StartByPass();
                XposedBridge.hookMethod(medErr,hookErr);
                ByPassInnerHook.EndBypass();
                //XposedBridge.log("小菜豆->Xposed_LogCatcher_InitSuccess");
            }catch (Throwable e2){
                StringPool_XpLog.Add("Can't get XposedLog.");
                StringPool_XpErr.Add("Can't get XposedLog.");
            }

        }

    }
}
