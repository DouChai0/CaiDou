package com.ldq.connect.MainWorker.LittileHook;

import com.ldq.Utils.MClass;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Disable_QQ_Auto_Update {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.upgrade.UpgradeController"), "a",
                MClass.loadClass("protocol.KQQConfig.UpgradeInfo"),
                MClass.loadClass("com.tencent.mobileqq.upgrade.UpgradeController$OnHandleUpgradeFinishListener"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽更新提醒",false))param.setResult(true);
                    }
                }
        );

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.upgrade.UpgradeTipsDialog"), "show",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽更新提醒",false))param.setResult(null);
                    }
                }
        );
    }
}
