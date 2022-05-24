package com.ldq.connect.ExceptionHelper;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.connect.MHookEnvironment;

import java.util.HashMap;
import java.util.HashSet;

import dalvik.system.DexClassLoader;

public class ExceptionUtils {

    public static String CollectModuleInfo() {
        HashSet<String> mPathList = new HashSet<>();
        String Maps1 = new String(FileUtils.ReadFileByte("/proc/self/maps"));
        if(!TextUtils.isEmpty(Maps1)) {
            int index1 = 0;
            int index2 = 0;
            index1 = Maps1.indexOf("/data/app/");
            while (index1!=-1) {
                index2 = Maps1.indexOf("\n",index1+10);
                if(index2!=-1) {
                    String Line = Maps1.substring(index1,index2);
                    if(Line.contains("base.apk")) {
                        mPathList.add(Line.substring(Line.indexOf("/data/app/"),Line.indexOf("base.apk")+8));
                    }
                }else {
                    break;
                }
                index1 = Maps1.indexOf("/data/app/",index2);
            }
        }

        Maps1 = new String(FileUtils.ReadFileByte("/proc/self/smaps"));
        if(!TextUtils.isEmpty(Maps1))
        {
            int index1;
            int index2;
            index1 = Maps1.indexOf("/data/app/");
            while (index1!=-1)
            {
                index2 = Maps1.indexOf("\n",index1+10);
                if(index2!=-1)
                {
                    String Line = Maps1.substring(index1,index2);
                    if(Line.contains("base.apk"))
                    {
                        mPathList.add(Line.substring(Line.indexOf("/data/app/"),Line.indexOf("base.apk")+8));
                    }

                }else
                {
                    break;
                }
                index1 = Maps1.indexOf("/data/app/",index2);
            }
        }


        String LineData = "";
        for(String s : mPathList)
        {
            LineData = LineData + GetApkLine(s);
        }
        if (TextUtils.isEmpty(LineData))return "NotFound";

        return LineData;
    }
    private static HashMap<String,ClassLoader> sModuleLoader = null;
    private static void CollectModuleClassLoader()
    {
        sModuleLoader = new HashMap<>();
        HashSet<String> mPathList = new HashSet<>();
        String Maps1 = new String(FileUtils.ReadFileByte("/proc/self/maps"));
        if(!TextUtils.isEmpty(Maps1))
        {
            int index1 = 0;
            int index2 = 0;
            index1 = Maps1.indexOf("/data/app/");
            while (index1!=-1)
            {
                index2 = Maps1.indexOf("\n",index1+10);
                if(index2!=-1)
                {
                    String Line = Maps1.substring(index1,index2);
                    if(Line.contains("base.apk"))
                    {
                        mPathList.add(Line.substring(Line.indexOf("/data/app/"),Line.indexOf("base.apk")+8));
                    }

                }else
                {
                    break;
                }
                index1 = Maps1.indexOf("/data/app/",index2);
            }
        }

        Maps1 = new String(FileUtils.ReadFileByte("/proc/self/smaps"));
        if(!TextUtils.isEmpty(Maps1))
        {
            int index1 = 0;
            int index2 = 0;
            index1 = Maps1.indexOf("/data/app/");
            while (index1!=-1)
            {
                index2 = Maps1.indexOf("\n",index1+10);
                if(index2!=-1)
                {
                    String Line = Maps1.substring(index1,index2);
                    if(Line.contains("base.apk"))
                    {
                        mPathList.add(Line.substring(Line.indexOf("/data/app/"),Line.indexOf("base.apk")+8));
                    }

                }else
                {
                    break;
                }
                index1 = Maps1.indexOf("/data/app/",index2);
            }
        }

        for(String s : mPathList)
        {
            try{
                if(IsXposedMode(s))
                {
                    ClassLoader BaseLoader = ClassLoader.getSystemClassLoader();
                    DexClassLoader sDexLoader = new DexClassLoader(s,
                            MHookEnvironment.MAppContext.getDataDir().toString(),
                            "",
                            BaseLoader
                    );

                    PackageManager pm = MHookEnvironment.MAppContext.getPackageManager();
                    PackageInfo sInfo =pm.getPackageArchiveInfo(s,0);
                    ApplicationInfo appinfo = sInfo.applicationInfo;
                    String AppName = pm.getApplicationLabel(appinfo).toString();

                    sModuleLoader.put(AppName,sDexLoader);
                }

            }catch (Exception e)
            {
                MLogCat.Print_Debug(Log.getStackTraceString(e));
            }
        }
    }
    private static ClassLoader BaseLoader = ClassLoader.getSystemClassLoader();
    public static String GetContainModuleInfo(String ClassName)
    {
        if(ClassName.startsWith("bsh."))return "";
        if(ClassName.startsWith("android"))return "";
        try{
            if(sModuleLoader==null)
            {
                CollectModuleClassLoader();
            }
            for(String s : sModuleLoader.keySet())
            {
                ClassLoader sLoader = sModuleLoader.get(s);
                if(sLoader!=null)
                {
                    try {
                        BaseLoader.loadClass(ClassName);
                        continue;
                    } catch (Exception e) {
                        try{
                            sLoader.loadClass(ClassName);
                            return s;
                        }catch (Exception ex)
                        {
                        }
                    }
                }
            }
        }catch (Throwable th)
        {}

        return "";
    }
    private static String GetApkLine(String ApkFile)
    {
        try{
            if(IsXposedMode(ApkFile))
            {
                String returnText = "";
                PackageManager pm = MHookEnvironment.MAppContext.getPackageManager();
                PackageInfo sInfo =pm.getPackageArchiveInfo(ApkFile,0);
                ApplicationInfo appinfo = sInfo.applicationInfo;

                String Name = appinfo.loadLabel(pm).toString();

                returnText = returnText + Name+"("+sInfo.packageName+":"+sInfo.versionName+")";

                return returnText+"\n";
            }
            return "";
        }catch (Throwable th)
        {
            MLogCat.Print_Debug(Log.getStackTraceString(th));
            return "";
        }

    }
    private static boolean IsXposedMode(String ApkFile)
    {
        try{
            PackageManager pm = MHookEnvironment.MAppContext.getPackageManager();
            PackageInfo sInfo =pm.getPackageArchiveInfo(ApkFile,PackageManager.GET_META_DATA);
            ApplicationInfo appinfo = sInfo.applicationInfo;
            if(appinfo.metaData.getBoolean("xposedmodule",false))
            {
                return true;
            }
            return false;
        }catch (Throwable th)
        {
            return false;
        }
    }
}
