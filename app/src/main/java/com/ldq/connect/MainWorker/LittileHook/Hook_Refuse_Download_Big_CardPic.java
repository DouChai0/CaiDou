package com.ldq.connect.MainWorker.LittileHook;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Refuse_Download_Big_CardPic
{
    public static long mStartTime=0;
    public static HashMap<String,Long> mCache = new HashMap<>();
    public static void Start()
    {
        try{
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.transfile.HttpDownloader","downloadImage", File.class,new Class[]{
                    OutputStream.class,
                    MClass.loadClass("com.tencent.image.DownloadParams"),
                    MClass.loadClass("com.tencent.image.URLDrawableHandler"),
                    int.class,
                    URL.class
            });
            XposedBridge.hookMethod(hookMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Object paramObj = param.args[1];
                    String mUrl = MField.GetField(paramObj,paramObj.getClass(),"urlStr",String.class);
                    if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽耗流量卡片",false)) {
                        long Size =0;
                        if(!mCache.containsKey(mUrl)) {
                            Size = HttpUtils.GetFileLength(mUrl);
                        } else {
                            Long mLong = mCache.get(mUrl);
                            Size = mLong.longValue();
                        }

                        //如果文件大于100M就直接停止加载并,并且缓存地址,防止重复获取大小信息消耗流量
                        if(Size>100*1024*1024 || mCache.containsKey(mUrl)) {
                            if(System.currentTimeMillis()>mStartTime+60*1000) {
                                Utils.ShowToast("当前下载的图片可能过大,已停止加载,图片大小:"+ DataUtils.bytes2kb(Size));
                                mStartTime = System.currentTimeMillis();
                                mCache.put(mUrl,Size);
                            }
                            param.setResult(null);
                        }
                    }
                }
            });
        }
        catch (Throwable ex)
        {
            MLogCat.Print_Error("Download Hook Error",ex);
        }

    }
}
