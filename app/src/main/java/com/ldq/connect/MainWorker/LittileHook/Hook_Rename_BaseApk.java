package com.ldq.connect.MainWorker.LittileHook;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Rename_BaseApk {
    static ArrayList<String> Uploading = new ArrayList<>();
    static class InvokeData{
        String str2;
        long sLong;
        int sInt;
        Object sObject;
    }
    static HashMap<String,InvokeData> UploadMap = new HashMap<>();
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.filemanager.app.FileManagerEngine"), "a",
                String.class,String.class,long.class,int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(!MConfig.Get_Boolean("Main","MainSwitch","上传重命名",false))return;
                        String Path = (String) param.args[0];
                        if(Path.endsWith("base.apk") || Path.startsWith("/data/app/") || (Path.contains("/cache/share/") && Path.endsWith(".apk") && Path.contains("_")))
                        {
                            String ChangeName = GetPackageInfo(Path);
                            if(ChangeName!=null)
                            {
                                FileUtils.copy(Path, MHookEnvironment.PublicStorageModulePath + "Cache/"+ChangeName+".apk",4096);
                                param.args[0] =MHookEnvironment.PublicStorageModulePath + "Cache/"+ChangeName+".apk";
                                Uploading.add(MHookEnvironment.PublicStorageModulePath + "Cache/"+ChangeName+".apk");

                                InvokeData in = new InvokeData();
                                in.str2 = (String) param.args[1];
                                in.sLong = (long) param.args[2];
                                in.sInt = (int) param.args[3];
                                in.sObject = param.thisObject;

                                UploadMap.put(MHookEnvironment.PublicStorageModulePath + "Cache/"+ChangeName+".apk",in);
                            }
                            else
                            {

                            }
                        }
                    }
                }
        );
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.filemanager.uftwrapper.QFileTroopTransferWrapper$TroopUploadWrapper"), "a",
                MClass.loadClass("com.tencent.mobileqq.uftransfer.api.IUFTTransferKey"), int.class,
                MClass.loadClass("com.tencent.mobileqq.uftransfer.api.IUFTUploadCompleteInfo"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(!MConfig.Get_Boolean("Main","MainSwitch","上传重命名",false))return;
                        Object item = MField.GetField(param.thisObject,"a", MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopFileTransferManager$Item"));
                        String Path = MField.GetField(item,"LocalFile",String.class);
                        int i = (int) param.args[1];
                        File f = new File(Path);
                        if(i==202)
                        {
                            FileUtils.copy(Path,Path+".rename",4096);
                            if(Uploading.contains(Path))
                            {
                                new File(Path).delete();
                                Uploading.remove(Path);
                                if(UploadMap.containsKey(Path))
                                {
                                    InvokeData ins = UploadMap.get(Path);
                                    MMethod.CallMethod(ins.sObject,"a",boolean.class,new Class[]{
                                            String.class,String.class,long.class,int.class
                                    },Path+".rename",ins.str2,ins.sLong,ins.sInt);
                                    UploadMap.remove(Path);
                                }
                            }


                        }else
                        {
                            if(Uploading.contains(Path))
                            {
                                new File(Path).delete();
                                Uploading.remove(Path);
                            }
                        }




                    }
                }
        );
    }
    public static String GetPackageInfo(String Path) {
        PackageManager manager = MHookEnvironment.MAppContext.getPackageManager();
        PackageInfo info = manager.getPackageArchiveInfo(Path,PackageManager.GET_ACTIVITIES);
        if(info!=null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = Path;
            appInfo.publicSourceDir = Path;
            String appName = manager.getApplicationLabel(appInfo).toString();
            String version = info.versionName;
            return appName + "-"+version;
        }
        return null;
    }
}
