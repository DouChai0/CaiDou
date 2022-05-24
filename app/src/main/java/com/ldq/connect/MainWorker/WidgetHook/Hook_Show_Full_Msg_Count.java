package com.ldq.connect.MainWorker.WidgetHook;

import android.view.ViewGroup;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;


public class Hook_Show_Full_Msg_Count {
    public static void Start()
    {
        XposedHelpers.findAndHookMethod("com.tencent.widget.CustomWidgetUtil", MHookEnvironment.mLoader, "a",
                TextView.class, int.class, int.class, int.class, int.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","显示消息数量",false))
                        {
                            param.args[4]=Integer.MAX_VALUE;
                        }
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","显示消息数量",false))
                        {
                            if(ClassTable.NowQQVersion > 5570)
                            {
                                int Type = (int) param.args[1];
                                if(Type == 4 || Type == 7 || Type == 9|| Type == 3)
                                {
                                    TextView t = (TextView) param.args[0];
                                    int Count = (int) param.args[2];
                                    String str = ""+Count;
                                    ViewGroup.LayoutParams params = t.getLayoutParams();
                                    params.width = Utils.dip2px(MHookEnvironment.MAppContext,9+7*str.length());
                                    t.setLayoutParams(params);
                                }

                            }
                        }

                    }
                });

    }
}
