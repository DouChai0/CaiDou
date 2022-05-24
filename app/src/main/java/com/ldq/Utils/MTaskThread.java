package com.ldq.Utils;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MTaskThread implements Runnable
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
            }catch (Throwable ex)
            {
                MLogCat.Print_Error("TASK_THREAD_CRASH",ex);
            }

        }

    }

    public void init()
    {
        mThread = new Thread(this);
        mThread.setName("LD_MODULE_TASK_THREAD");
        mThread.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void Print(){
        StackTraceElement[] track = mThread.getStackTrace();
        for(StackTraceElement t : track){
            MLogCat.Print_Debug(t.getClassName()+"."+t.getMethodName()+"("+t.getFileName()+":"+t.getLineNumber()+")");
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
        AtomicBoolean Ato = new AtomicBoolean();

        Ato.set(false);
        mHandler.post(() -> Ato.getAndSet(true));
        while (Ato.get())
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
