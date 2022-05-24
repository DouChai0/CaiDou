package com.ldq.connect.MainWorker.BaseWorker.EnvHook;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.Init;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MTool.AutoCheckSign;
import com.ldq.connect.MTool.AutoMessage;
import com.ldq.connect.MTool.FriendObserve.FriendsObserve;
import com.ldq.connect.QQUtils.BaseInfo;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class StartDelayHook {
    static XC_MethodHook.Unhook un;
    public static void DelayHook()
    {
        try {
            //在QQ加载数据时进行的延迟Hook,这时QQ的部分数据已经被初始化,可以使用了
            un = XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.startup.step.LoadData"), "doStep", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    DelayHookLoad();
                    try{
                        un.unhook();
                    }catch (Throwable th)
                    {
                        Utils.ShowToast("我不知道是你的框架垃圾还是你自己改了什么东西,连unhook都会出错,这是不应该发生的:\n"+th);
                    }


                }
            });
        } catch (ClassNotFoundException e) {
            Utils.ShowToast("小菜豆->模块初始化时发生错误:\n"+e);
        }
    }
    //延迟初始化的一些数据或者任务计划
    private static void DelayHookLoad()
    {
        try{
            new Thread(()->{
                //延迟进行一些参数的更新
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                FriendsObserve.RefreshFriendsInfo();//刷新缓存的好友信息
            }).start();

            JavaPlugin.InitDexDir();

            MHookEnvironment.AppInterface = BaseInfo.GetAppInterface();

            AutoCheckSign.InitCheckTask();

            //MHookEnvironment.mTask.PostTask(()-> DpiHook.ShowDpiCalcelDialog(Utils.GetThreadActivity()),5000);

            MHookEnvironment.mTask.PostTask(()-> {
                try {
                    MHookEnvironment.AppInterface = BaseInfo.GetAppInterface();
                } catch (Exception e) {
                    MLogCat.Print_Error("GetDelayAppinterface",e);
                }
            },5000);

            new Thread(()-> {
                try {
                    Init.InitRes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            MHookEnvironment.mTask.PostTask(()->JavaPlugin.CheckAutoLoadPlugin());

            MHookEnvironment.mTask.PostTask(()-> AutoMessage.InitTaskInfo(),5000);

            FriendsObserve.StartCheck();
        }catch (Throwable th)
        {
            Utils.ShowToast("DelayHook Error:\n"+th);
        }
    }
}
