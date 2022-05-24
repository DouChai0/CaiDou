package com.ldq.connect.MainWorker.LittileHook;

import android.graphics.Bitmap;
import android.util.Log;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Upload_Transparent_Pic {
    public static void Start()
    {
        try{
            XposedHelpers.findAndHookMethod(Bitmap.class, "compress", Bitmap.CompressFormat.class, int.class, OutputStream.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //判断为QQ上传头像处调用才拦截
                    //TroopUploadingThread
                        String CurrentCallStacks = Utils.getCurrentCallStacks();
                        if (CurrentCallStacks.contains("NearbyPeoplePhotoUploadProcessor") || CurrentCallStacks.contains("doInBackground") ||
                                CurrentCallStacks.contains("TroopUploadingThread")) {
                            param.args[0] = Bitmap.CompressFormat.PNG;
                        }
                }
            });
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.pic.compress.Utils","a",boolean.class,new Class[]{
                    String.class,
                    Bitmap.class,
                    int.class,
                    String.class,
                    MClass.loadClass("com.tencent.mobileqq.pic.CompressInfo")
            });
            XposedBridge.hookMethod(hookMethod,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                                try {
                                    //自己进行图像转换,不给QQ把透明背景扣掉的机会
                                    FileOutputStream fos = new FileOutputStream((String) param.args[0]);
                                    Bitmap bitmap = (Bitmap) param.args[1];
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.close();
                                    param.setResult(true);
                                } catch (Throwable e) {
                                    MLogCat.Print_Error("aError", e);
                                }
                        }
                    });

            Method med = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.vip.VipStatusManagerImpl"),"getPrivilegeFlags",int.class,new Class[]{String.class});
            XposedBridge.hookMethod(med, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                        String uin = (String) param.args[0];
                        if(uin==null)
                        {
                            int i = (int) XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                            param.setResult(i | 2 | 4 | 8);
                        }
                }
            });
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("PICUpload",Log.getStackTraceString(th));
        }

    }
}
