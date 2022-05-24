package com.ldq.connect.MainWorker.WidgetHook;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Hide_QQ_Top_Toast {
    public static void Start()
    {
        try{
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.widget.QQToast$ProtectedToast","show",void.class);
            XposedBridge.hookMethod(hookMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Toast toast = (Toast) param.thisObject;
                    //取得Toast的View信息
                    //QQ的Toast是自定义View的,一般有一个ImageView和TextView
                    //我只需要关注TextView就行了
                    //MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
                    ViewGroup vg = (ViewGroup) toast.getView();
                    if(vg==null) return;
                    //取出TextView实例
                    TextView textView = (TextView) traversalView(vg);
                    //如果不纯在TextView直接返回
                    if(textView!=null)
                    {
                        String ToastText = (String) textView.getText();

                        //条件1

                        if(ToastText.equals("转发成功")){
                            param.setResult(null);
                        }


                        if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽烦人提示",true))
                        {
                            if(ToastText.equals("语音转换文字失败") || ToastText.equals("资源域名已拦截") || ToastText.contains("设置禁言")|| ToastText.contains("解除禁言"))
                            {
                                param.setResult(null);
                            }
                        }



                        //条件2
                        if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽部分提示",false))
                        {
                            if(!Utils.isTopActivity("com.tencent.mobileqq"))
                            {
                                param.setResult(null);
                            }
                        }
                    }
                }
            });
        }

        catch (Throwable ex)
        {

        }
    }
    public static View traversalView(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                View v6=traversalView((ViewGroup) view);
                if(v6!=null) return v6;
            } else {
                if(doView(view)) return view;
            }
        }
        return null;
    }
    private static boolean doView(View view) {
        if (view.getClass().getSimpleName().equals("TextView"))
        {
            return true;
        }
        return false;

    }
}
