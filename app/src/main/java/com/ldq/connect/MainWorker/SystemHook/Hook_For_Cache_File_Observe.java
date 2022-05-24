package com.ldq.connect.MainWorker.SystemHook;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MTool.QQCleaner.ChileFileChecker;
import com.ldq.connect.QQUtils.BaseInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Cache_File_Observe {

    static volatile boolean OpenStatus = false;

    public static void Start(){
        //获取开关状态
        OpenStatus = GlobalConfig.Get_Boolean("缓存控制",false);
        if(OpenStatus) {
            new Thread(Hook_For_Cache_File_Observe::ObserveThread).start();
            StartHook();
        }
        //启动首次收集线程
        if(GlobalConfig.Get_Boolean("缓存控制收集",false)){
            new Handler(Looper.getMainLooper()).postDelayed(()->CollectInfosFirst(),3000);
        }


    }
    static AtomicBoolean CheckingFileinfo = new AtomicBoolean();
    private static void StartHook(){
        XposedHelpers.findAndHookMethod(File.class, "exists", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                boolean ExistResult = (boolean) param.getResult();
                if(OpenStatus && !CheckingFileinfo.get() && ExistResult && ((File)param.thisObject).isFile()){
                    String RawPath = ((File)param.thisObject).getAbsolutePath();
                    if(RawPath.contains("/ptt/") && RawPath.contains("/"+BaseInfo.GetCurrentUin()+"/ptt/")) {
                        onPathChange(RawPath);
                    }else if (RawPath.contains("/Tencent/MobileQQ/chatpic/")) {
                        onPathChange(RawPath);
                    }else if (RawPath.contains("/Tencent/MobileQQ/diskcache/")) {
                        onPathChange(RawPath);
                    }else if(RawPath.contains("/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/")) {
                        onPathChange(RawPath);
                    }
                }
            }
        });

    }
    private static void onPathChange(String FullPath){
        if(!OpenStatus)return;
        new File(FullPath).setLastModified(System.currentTimeMillis());
    }

    private static void CollectInfosFirst(){
        GlobalConfig.Put_Boolean("缓存控制收集",false);
        Context context = Utils.GetThreadActivity();
        prog = new ProgressDialog(context,3);
        prog.setTitle("缓存时间控制");
        prog.setCancelable(false);
        prog.show();

        new Thread(Hook_For_Cache_File_Observe::CollectInfosThread).start();

    }
    static ProgressDialog prog;
    private static void CollectInfosThread(){

        GlobalConfig.Put_Long("最后检测缓存时间",System.currentTimeMillis());
        try{
            CheckingFileinfo.getAndSet(true);
            new Handler(Looper.getMainLooper()).post(()->prog.setMessage("正在处理数据1/5"));
            CheckFirstIn(new File(Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"+BaseInfo.GetCurrentUin()+"/ptt/"));
            new Handler(Looper.getMainLooper()).post(()->prog.setMessage("正在处理数据2/5"));
            CheckFirstIn(new File(Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/"));
            new Handler(Looper.getMainLooper()).post(()->prog.setMessage("正在处理数据3/5"));
            CheckFirstIn(new File(Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/diskcache/"));
            new Handler(Looper.getMainLooper()).post(()->prog.setMessage("正在处理数据4/5"));
            CheckFirstIn(new File(Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/"));
            new Handler(Looper.getMainLooper()).post(()->prog.setMessage("正在处理数据5/5"));
            CheckFirstIn(new File(Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/shortvideo/"));


        }finally {
            CheckingFileinfo.getAndSet(false);
            new Handler(Looper.getMainLooper()).post(()->prog.dismiss());
            try {
                Buffer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private static void CheckTrueFile(File CheckFile){
        CheckFile.setLastModified(System.currentTimeMillis());
    }
    private static void CheckFirstIn(File Path){
        try{
            if(Path.isFile()){
                CheckTrueFile(Path);
                return;
            }
            File[] list = Path.listFiles();
            if(list==null)return;
            for(File fs : list){
                if(fs.isFile()){
                    CheckTrueFile(fs);
                    return;
                }
                if(fs.isDirectory() && !fs.getName().equals(".") && !fs.getName().equals(".."))
                {
                    CheckFirstIn(fs);
                }
            }

            File[] CheckLast = Path.listFiles();
            if(CheckLast==null || CheckLast.length ==0)Path.delete();
        }catch (Exception e){

        }
    }

    volatile static LinkedList<String> WillClearItemList = new LinkedList<>();
    private static void CacheListClear(){
        //MLogCat.Print_Debug("Checker_Event");
        long LastClean = GlobalConfig.Get_Long("上次清理时间",0);
        if(System.currentTimeMillis() - LastClean > 1000 * 3600){
            GlobalConfig.Put_Long("上次清理时间",System.currentTimeMillis());

            ChileFileChecker.Check_Path_JustFile(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/" + BaseInfo.GetCurrentUin() + "/ptt/"));
            ChileFileChecker.Check_Path_JustFile(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/"));
            ChileFileChecker.Check_Path_JustFile(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/diskcache/"));
            ChileFileChecker.Check_Path_JustFile(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/"));
            ChileFileChecker.Check_Path_JustFile(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/shortvideo/"));
        }
    }
    private static void TrueClearAnt(){
        if(WillClearItemList!=null && WillClearItemList.size()>0){
            for(String RemoveFile : WillClearItemList){
                ClearLog(RemoveFile);
                new File(RemoveFile).delete();
            }
        }
    }
    static AtomicInteger FailedCount = new AtomicInteger();
    static BufferedWriter Buffer;
    public static void ClearLog(String LogClearLine){
        if(FailedCount.get()>10)return;
        try{
            if(Buffer == null){
                Buffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(MHookEnvironment.PublicStorageModulePath + "Log/CacheDel.log")));
            }

            Buffer.write("["+ Utils.GetNowTime()+"]"+LogClearLine);
            Buffer.newLine();
            FailedCount.getAndSet(0);
        }catch (Exception e)
        {
            try{
                Buffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(MHookEnvironment.PublicStorageModulePath + "Log/CacheDel.log")));
                Buffer.write("["+ Utils.GetNowTime()+"]"+LogClearLine);
                Buffer.newLine();
                FailedCount.getAndSet(0);
            }catch (Exception ex){
                FailedCount.incrementAndGet();
            }

        }

    }
    private static void ObserveThread(){
        while (true){
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
                CacheListClear();

                TrueClearAnt();
                try {
                    Buffer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }finally {

            }

        }
    }

}
