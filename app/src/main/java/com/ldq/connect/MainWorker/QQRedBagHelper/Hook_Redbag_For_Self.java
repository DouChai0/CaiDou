package com.ldq.connect.MainWorker.QQRedBagHelper;

import android.text.TextUtils;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.Tools.Pinyin;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Redbag_For_Self {
    static Object MyInfo = null;
    public static void Start() throws Exception {

        /*


        XposedBridge.hookAllConstructors(MClass.loadClass("com.tencent.mobileqq.selectmember.SelectedAndSearchBar"), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
            }
        });

         */


        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.selectmember.SelectMemberActivity"), "getIntentExtras", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if(MConfig.Get_Boolean("Main","MainSwitch","专属发自己",false))
                {
                    MField.SetField(param.thisObject,"mShowMyself",true);
                    MField.SetField(param.thisObject,"mIsPutMySelfFirst",true);
                }

            }
        });
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.data.QQWalletAioBodyReserve"), "init",byte[].class,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if(!MConfig.Get_Boolean("Main","MainSwitch","生僻字",false))return;
                    String md5 = MField.GetField(param.thisObject,"shengpiziMD5",String.class);
                    if(!TextUtils.isEmpty(md5))
                    {
                        String Result = Pinyin.GetMD5Pinyin(md5);
                        if(!TextUtils.isEmpty(Result)) MField.SetField(param.thisObject,"shengpiziMask",Result );
                    }
                }
            });

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.data.QQWalletRedPacketMsg"), "readExternal", MClass.loadClass("com.tencent.mobileqq.data.QwSafeInputStream"),new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if(!MConfig.Get_Boolean("Main","MainSwitch","生僻字",false))return;
                    String md5 = MField.GetField(MField.GetField(param.thisObject,"body"),"shengpiziMD5",String.class);
                    if(!TextUtils.isEmpty(md5))
                    {
                        String Result = Pinyin.GetMD5Pinyin(md5);
                        if(!TextUtils.isEmpty(Result))   MField.SetField(MField.GetField(param.thisObject,"body"),"shengpiziMask", Result);
                    }
                }
            });
        }catch (Throwable th)
        {

        }





    }

}
