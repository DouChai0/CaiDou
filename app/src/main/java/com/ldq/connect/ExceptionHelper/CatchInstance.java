package com.ldq.connect.ExceptionHelper;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import com.ldq.Utils.MClass;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import com.tencent.rmonitor.looper.LooperProxy;

public class CatchInstance {
    public static void StartCatch(){
        if(GlobalConfig.Get_Boolean("关闭阻止闪退",false))return;
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (t.getName().equals("main")){
                ThreadToast("小菜豆->QQ主线程崩溃,即将退出:\n"+e);
                ICatchEx(Thread.currentThread(),e);
                return;
            }
            ICatchEx(t,e);
        });
        HandlerCatcher();
        XposedLogCatcher.StartCatcher();
        LogcatCatcher.StartCatch();
        FixUEH();
        XposedBridge.log("小菜豆->阻止闪退初始化完成");
    }
    private static void FixUEH(){
        try {
            XposedHelpers.findAndHookMethod("java.lang.Thread", MHookEnvironment.mLoader, "setDefaultUncaughtExceptionHandler", MClass.loadClass("java.lang.Thread$UncaughtExceptionHandler"), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable { super.beforeHookedMethod(param);
                    param.setResult(null); }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void HandlerCatcher(){
        LooperProxy.Proxy();
    }
    public static void ThreadToast(String vText)
    {
        JavaPlugin.mTask.PostTask(()->{
            if(MHookEnvironment.MAppContext==null) return;
            Toast.makeText(MHookEnvironment.MAppContext,vText,Toast.LENGTH_LONG).show();
        });
    }
    static String InitPackVersion()
    {
        try {
            //获取普通apk版本信息
            PackageManager pm = MHookEnvironment.MAppContext.getPackageManager();
            PackageInfo mSelfInfo = pm.getPackageInfo("com.tencent.mobileqq",0);
            String Version_1 = mSelfInfo.versionName;

            //获取QQ小版本信息
            ApplicationInfo sAppInfo = pm.getApplicationInfo("com.tencent.mobileqq",PackageManager.GET_META_DATA);
            String UUID = sAppInfo.metaData.getString("com.tencent.rdm.uuid");

            return Version_1+"."+UUID.substring(0,UUID.indexOf("_"));
        } catch (Throwable e) {
            XposedBridge.log(e);
            return "";
        }
    }
    public static void ICatchEx(Thread thread,Throwable th){
        CatcherPacker packer = CatcherPacker.getInstance();
        //收集Throwable信息
        StringBuilder thOut = new StringBuilder();
        thOut.append("ProcessName:").append(MHookEnvironment.ProcessName).append("\n");
        thOut.append("ThreadName:").append(thread.getName()).append("\n");
        thOut.append("XposedTag:").append(CollectBridgeTag()).append("\n");
        thOut.append("XposedTagDec:").append(TGetFrameName()).append("\n");
        thOut.append("QQVersion:").append(InitPackVersion()).append("\n");
        thOut.append("Time:").append(Utils.GetNowTime()).append("\n");
        thOut.append("AndroidVersion:").append(Build.VERSION.RELEASE).append("\n");
        thOut.append("SDK_Level:").append(Build.VERSION.SDK_INT).append("\n");
        thOut.append("CPU_ABI:").append(Build.CPU_ABI).append("\n");
        thOut.append("MODEL:").append(Build.MODEL).append("\n");
        thOut.append("BRAND:").append(Build.BRAND).append("\n");
        thOut.append("BOARD:").append(Build.BOARD).append("\n");
        thOut.append("DEVICE:").append(Build.DEVICE).append("\n");
        thOut.append("PRODUCT:").append(Build.PRODUCT).append("\n");
        thOut.append("----Module_List----\n").append(ExceptionUtils.CollectModuleInfo()).append("\n-------------------");




        thOut.append("\n");
        thOut.append(th.toString()).append("\n");
        thOut.append(CollectThrow(th));

        packer.AddStackTrace(thOut.toString());

        //收集Logcat信息
        packer.AddLogcatInfo(StringPool_Logcat.getAll());
        packer.AddXposedLog(StringPool_XpLog.getAll());
        packer.AddXposedErr(StringPool_XpErr.getAll());

        packer.CloseAll();


        Utils.ShowToast("小菜豆->已阻止QQ闪退:\n"+th.toString()+"\n" +
                "日志已保存到路径:"+packer.getPath());


    }
    private static String TGetFrameName(){
        String Tag = CollectBridgeTag();
        if (Tag.equals("BugHook"))return "应用转生";
        if (Tag.equals("LSPosed-Bridge"))return "LSPosed";
        if (Tag.equals("SandXposed"))return "天鉴";
        if (Tag.equals("PineXposed"))return "DreamLand";
        if (Tag.equals("Xposed")){
            try{
                Class clz = XposedBridge.class.getClassLoader()
                        .loadClass("me.weishu.exposed.ExposedBridge");
                if (clz != null)return "太极";
            }catch (Exception e){

            }
        }
        return "未知";
    }
    private static String CollectBridgeTag(){
        String BUGTag = CheckIsBugHook();
        if (BUGTag == null){
            try{
                Field f = XposedBridge.class.getField("TAG");
                f.setAccessible(true);
                return (String) f.get(null);
            }catch (Exception e){
                return "未知";
            }
        }
        return BUGTag;
    }
    private static String CheckIsBugHook(){
        ClassLoader BridgeLoader = XposedBridge.class.getClassLoader();
        try{
            Class clz = BridgeLoader.loadClass("com.bug.hook.xposed.HookBridge");
            Field Tag = clz.getField("TAG");
            Tag.setAccessible(true);
            return (String) Tag.get(null);
        }catch (Exception e){
            return null;
        }
    }
    private static String CollectThrow(Throwable th){
        StringBuilder builder = new StringBuilder();
        builder.append(th.toString()).append("\n");
        StackTraceElement[] stack = th.getStackTrace();
        for (StackTraceElement element : stack){
            builder.append("at ").append(element.getClassName()).append(".")
                    .append(element.getMethodName());
            if(element.isNativeMethod()){
                builder.append("(Native Method)").append("\n");
            }else {
                builder.append("(").append(element.getFileName()).append(":")
                        .append(element.getLineNumber()).append(")").append("\n");
            }
        }
        builder.append("\n\n");
        Throwable Up = th.getCause();
        if (Up != null){
            builder.append("-----------上层Throwable----------\n");
            builder.append(CollectThrow(Up));
        }
        return builder.toString();
    }
}
