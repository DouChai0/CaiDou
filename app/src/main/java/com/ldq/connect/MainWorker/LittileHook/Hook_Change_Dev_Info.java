package com.ldq.connect.MainWorker.LittileHook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.EditText;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseCall;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Change_Dev_Info {
    static String ChangeInfo;
    public static void Start()
    {
        try {
            ChangeInfo = MConfig.Get_String("Main","MainSet","DevInfo");
            if(!TextUtils.isEmpty(ChangeInfo.trim())){
                MField.SetField(null, Build.class,"MODEL",ChangeInfo);
                try{
                    XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.Pandora.deviceInfo.DeviceInfoManager"), "getModel",
                            Context.class, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    param.setResult(ChangeInfo);
                                }
                            }
                    );
                }catch (Throwable th){
                    XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.pandora.deviceinfo.DeviceInfoManager"), "h",
                            Context.class, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    param.setResult(ChangeInfo);
                                }
                            }
                    );
                }

            }


        } catch (Throwable exception) {
            MLogCat.Print_Error("ChangeDeviceInfo",exception);
        }
    }
    public static void StartDialog(Context context)
    {
        String ChangeInfo = MConfig.Get_String("Main","MainSet","DevInfo");
        if(TextUtils.isEmpty(ChangeInfo))ChangeInfo = Build.MODEL;
        EditText ed = new EditText(context);
        ed.setTextSize(18);
        ed.setText(ChangeInfo);

        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                .setView(ed)
                .setTitle("修改设备名(重启QQ生效)")
                .setNeutralButton("保存并重启", (dialog, which) -> {
                    String str = ed.getText().toString();
                    MConfig.Put_String("Main","MainSet","DevInfo",str);
                    RemoveDeviceInfoCache();
                    new Thread(()->{
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        BaseCall.ExitQQAnyWays();
                    }).start();


                }).show();
    }
    private static void RemoveDeviceInfoCache(){

        new File("/data/data/com.tencent.mobileqq/app_x5webview/Default/Local Storage/leveldb/MANIFEST-000001").delete();
        new File(MHookEnvironment.AppPath+"/app_x5webview/Default/Local Storage/leveldb/MANIFEST-000001").delete();
    }
}
