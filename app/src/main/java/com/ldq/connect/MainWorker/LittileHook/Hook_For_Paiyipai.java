package com.ldq.connect.MainWorker.LittileHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Paiyipai {
    public static void Start() throws Exception {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler","a",boolean.class,
                new Class[]{String.class});
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(true);
            }
        });

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler"), "a",
                String.class, String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","拍一拍连拍",false))
                        {
                            param.setResult(null);
                            Activity act = Utils.GetThreadActivity();
                            EditText ed = new EditText(act);
                            ed.setText("1");
                            ed.setTextSize(16);

                            new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                                    .setTitle("输入次数(每天上限约200次左右)")
                                    .setView(ed)
                                    .setNeutralButton("发送", (dialog, which) -> {
                                        int i = Integer.parseInt(ed.getText().toString());
                                        for(int is=0;is<i;is++)
                                        {
                                            try {
                                                XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                                            } catch (Exception e) {
                                                break;
                                            }
                                        }
                                    }).show();
                        }
                    }
                }
        );

        Method WillHook = MMethod.FindMethod("com.tencent.mobileqq.utils.ContactUtils","b",String.class,new Class[]{
                MClass.loadClass("com.tencent.common.app.AppInterface"),String.class,String.class
        });
        XposedBridge.hookMethod(WillHook, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","显示拍一拍",false))
                {
                    if(Log.getStackTraceString(new Throwable()).contains("UniteGrayTipUtil.a"))
                    {
                        String result = param.getResult()+"";
                        result = result+"("+param.args[2]+")";
                        param.setResult(result);
                    }
                }

            }
        });
    }
}
