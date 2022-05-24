package com.ldq.connect.MainWorker.LittileHook;

import android.view.View;

import com.ldq.Utils.MClass;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_OneClick_20Like {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.VisitorsActivity"), "onClick", View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View v = (View) param.args[0];
                if(MConfig.Get_Boolean("Main","MainSwitch","一键20赞",false))
                {
                    CharSequence s = v.getContentDescription();
                    if(s!=null)
                    {
                        if(s.toString().equals("赞"))
                        {
                            for(int i=0;i<19;i++)
                            {
                                XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                            }
                        }
                    }
                }
            }
        });


        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.profilecard.base.component.AbsProfileHeaderComponent"),
                "handleVoteBtnClickForGuestProfile",
                MClass.loadClass("com.tencent.mobileqq.data.Card"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","一键20赞",false))
                        {
                            for(int i=0;i<19;i++)
                            {
                                XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                            }
                        }
                    }
                }
        );

    }
}
