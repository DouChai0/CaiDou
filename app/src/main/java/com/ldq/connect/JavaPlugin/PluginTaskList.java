package com.ldq.connect.JavaPlugin;

import android.os.Handler;
import android.os.Looper;

import com.ldq.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class PluginTaskList implements Runnable
{
    Handler mHandler = null;
    Thread mThread = null;
    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(Looper.myLooper());
        while (true)
        {
            try{
                Looper.loop();
            }catch (Throwable th)
            {
                Utils.ShowToast("脚本队列意外崩溃,已经重启,崩溃原因:\n"+th);
            }
        }


    }

    public void init()
    {
        mThread = new Thread(this);
        mThread.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void PostTask(Runnable run)
    {
        mHandler.post(run);
    }
    public void PostTask(Runnable run,long Delay)
    {
        mHandler.postDelayed(run,Delay);
    }
    public void PostTaskAndWait(Runnable run)
    {
        mHandler.post(run);
        boolean[] bFlag = {false};
        mHandler.post(() -> bFlag[0]=true);
        while (bFlag[0]==false)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static class InvokeClass
    {
        public mRunTask mInvoke;
        public ArrayList mList;
    }
    public interface mRunTask
    {
        void mRunTask(ArrayList TaskArgs);
    }

    public static HashMap<String,InvokeClass> mRunTaskList = new HashMap<>();
    public static void RemoveThreadClock(String RunID)
    {
        mRunTaskList.remove(RunID);
    }
    public static void InitClockThread()
    {
        new Thread(()->{
            for (;;)
            {
                try{
                    Thread.sleep(1000);
                    for(String mTaskID : mRunTaskList.keySet())
                    {
                        try{
                            InvokeClass mTask = mRunTaskList.get(mTaskID);
                            mTask.mInvoke.mRunTask(mTask.mList);
                        }
                        catch (Throwable th)
                        {
                            continue;
                        }
                    }
                }
                catch (Throwable th)
                {}
            }

        }).start();
    }
    public static void AddThreadClock(String RunID,mRunTask mRunTask,ArrayList mArgs)
    {
        InvokeClass mClass = new InvokeClass();
        mClass.mInvoke = mRunTask;
        mClass.mList = mArgs;
        mRunTaskList.put(RunID,mClass);

    }
}
