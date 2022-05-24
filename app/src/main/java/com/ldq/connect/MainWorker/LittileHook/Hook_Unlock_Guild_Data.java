package com.ldq.connect.MainWorker.LittileHook;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Unlock_Guild_Data {
    public static void Start(){
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.setting.popup.GuildMainSettingDialogFragment"), "o",new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    View v = MField.GetField(param.thisObject,"o", RelativeLayout.class);
                    v.setVisibility(View.VISIBLE);
                    v.setOnClickListener((View.OnClickListener) param.thisObject);
                }
            });
        }catch (Exception e){
            XposedBridge.log(e);
        }
    }
}
