package com.ldq.connect.MainWorker.ProxyHook;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.Utils.MClass;
import com.ldq.connect.JavaPlugin.JavaPlugin;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Guild_Msg_Proxy {
    public static void Start(){
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.GuildOnlineMessageProcessor"), "c",
                    ClassTable.MessageRecord(),
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            JavaPlugin.OnGuildMessage(param.args[0],false);
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.api.impl.GuildMessageUtilsApiImpl"), "handleSelfSendMsg",
                    MClass.loadClass("com.tencent.common.app.AppInterface"), ClassTable.MessageRecord(), ClassTable.MessageRecord(), int.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(param.args[1].getClass().getSimpleName().equals("MessageRecord"))return;
                            JavaPlugin.OnGuildMessage(param.args[1],true);
                        }
                    }
            );
        }catch (Throwable th){

        }
    }
}
