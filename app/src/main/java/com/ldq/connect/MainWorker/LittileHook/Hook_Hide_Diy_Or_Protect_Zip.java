package com.ldq.connect.MainWorker.LittileHook;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Hide_Diy_Or_Protect_Zip
{
    public static void Start()
    {
        try{
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.profilecard.processor.TempProfileBusinessProcessor","updateCardTemplate", void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.Card"),
                    String.class,
                    MClass.loadClass("SummaryCardTaf.SSummaryCardRsp")
            });
            XposedBridge.hookMethod(hookMethod, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽DIY名片",false))
                    {
                        try{
                            for(Field fie:param.args[0].getClass().getDeclaredFields())
                            {
                                //设置所有卡片的StyleID为0,这样所有的DIY卡片都不会显示了
                                //其实判断一下可能更准确
                                //不过不判断也没啥问题,就这样吧
                                if(fie.getName().equals("lCurrentStyleId"))
                                {
                                    fie.setAccessible(true);
                                    fie.setLong(param.args[0],0);
                                }
                            }
                        }
                        catch (Throwable e)
                        {
                            MLogCat.Print_Error("DIY名片处理错误",e);
                        }
                    }
                }
            });
            FuckDIYBoom();
        }
        catch (Throwable ex)
        {
            MLogCat.Print_Error("Hook Error",ex);
        }

    }
    public static void FuckDIYBoom()
    {
        try{
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.vas.widget.lottie.LottieLoader","unZipFile", void.class,new Class[]{
                    File.class,
                    String.class
            });
            XposedBridge.hookMethod(hookMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if(!MConfig.Get_Boolean("Main","MainSwitch","屏蔽名片炸弹",false)) return;
                    try{
                        File zipFile = (File)param.args[0];
                        long fileSize = zipFile.length();
                        ZipFile mZip = new ZipFile((File)param.args[0]);

                        Enumeration entries = (Enumeration<ZipEntry>) mZip.entries();
                        int i=0;
                        long sunSizes = 0;
                        while(entries.hasMoreElements()){//依次访问各条目
                            //如果文件数太多直接退出,防止卡顿
                            if(++i>100000) break;
                            ZipEntry ze = (ZipEntry) entries.nextElement();
                            //记录压缩包每个文件的大小加起来
                            sunSizes += ze.getSize();
                        }
                        String toastMessage = "";
                        //如果压缩包解压增量为100倍,极大几率为内存炸弹
                        if(fileSize*100>0)
                        {
                            if(fileSize*100<sunSizes)
                            {
                                toastMessage = "当前处理的压缩包文件存在异常\n解压前大小:"+fileSize+"字节("+ DataUtils.bytes2kb(fileSize)+")";
                                toastMessage = toastMessage + "\n解压后大小:"+sunSizes+"字节("+ DataUtils.bytes2kb(sunSizes)+")";
                                toastMessage = toastMessage +"\n\n已终止解压";
                            }
                        }
                        if(!toastMessage.isEmpty())
                        {
                            //直接删除压缩包,就不会解压了
                            //zipFile.delete();
                            final Activity act = Utils.GetThreadActivity();
                            if(act!=null)
                            {
                                final String finalToastMessage = toastMessage;
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(act, finalToastMessage,Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            param.setResult(null);
                        }

                    }
                    catch (Throwable e)
                    {
                        MLogCat.Print_Error("CheckZipError",Log.getStackTraceString(e));
                    }
                }
            });
        }
        catch (Throwable ex)
        {
            MLogCat.Print_Error("Hook DIY Error",ex);
        }

    }
}
