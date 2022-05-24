package com.ldq.connect.MainWorker.WidgetHook;

import android.content.Context;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.QQUtils.QQTools;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Show_Accurate_Unmute_Time {
    public static void Start()
    {
        try{
            Method method = MMethod.FindMethod("com.tencent.mobileqq.troop.troopgag.api.impl.TroopGagServiceImpl","gagTimeToStringCountDown",String.class,new Class[]{
                    Context.class,long.class
            });
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    long Time = (long) param.args[1]+30;
                    long ServletTime = QQTools.GetServerTime();
                    Time = Time - ServletTime;
                    if(Time<=0)
                    {
                        param.setResult("[解禁了,返回再进吧]");
                        return;
                    }
                    param.setResult(Utils.secondToTime(Time));
                }
            });

            method = MMethod.FindMethod("com.tencent.mobileqq.troop.utils.TroopGagMgr","a",String.class,new Class[]{
                    Context.class,long.class,long.class
            });
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    long Time = (long) param.args[1];
                    if(Time<=0)
                    {
                        param.setResult("[0秒]");
                        return;
                    }
                    param.setResult(Utils.secondToTime(Time));
                }
            });

        }catch (Throwable th)
        {
            MLogCat.Print_Error("GagInfo",th);
        }

    }
}
