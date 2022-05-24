package com.ldq.Utils;

import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

public class DebugUtils {
    public static void PrintAllStacks()
    {
        Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
        Log.e("albertThreadDebug","all start==============================================");
        for (Map.Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackElements = entry.getValue();
            MLogCat.Print_Debug("ThreadName:  "+thread.getName());
            MLogCat.Print_Debug("");
            MLogCat.Print_Debug("");
            for (int i = 0; i < stackElements.length; i++) {
                StringBuilder stringBuilder = new StringBuilder("    ");
                stringBuilder.append(stackElements[i].getClassName()+".")
                        .append(stackElements[i].getMethodName()+"(")
                        .append(stackElements[i].getFileName()+":")
                        .append(stackElements[i].getLineNumber()+")");
                MLogCat.Print_Debug("at "+stringBuilder.toString());
            }
            MLogCat.Print_Debug("");
            MLogCat.Print_Debug("");
        }
        Log.e("albertThreadDebug","all end==============================================");
    }
    public static void PrintMainStacks()
    {
        Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
        Log.e("albertThreadDebug","all start==============================================");
        for (Map.Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet()) {
            Thread thread = entry.getKey();
            if(!thread.getName().equals("main"))continue;
            StackTraceElement[] stackElements = entry.getValue();
            MLogCat.Print_Debug("ThreadName:  "+thread.getName());
            MLogCat.Print_Debug("");
            MLogCat.Print_Debug("");
            for (int i = 0; i < stackElements.length; i++) {
                StringBuilder stringBuilder = new StringBuilder("    ");
                stringBuilder.append(stackElements[i].getClassName()+".")
                        .append(stackElements[i].getMethodName()+"(")
                        .append(stackElements[i].getFileName()+":")
                        .append(stackElements[i].getLineNumber()+")");
                MLogCat.Print_Debug("at "+stringBuilder.toString());
            }
            MLogCat.Print_Debug("");
            MLogCat.Print_Debug("");
        }
        Log.e("albertThreadDebug","all end==============================================");
    }
    public static void PrintAllField_direct(Object mObj)
    {

        Class mClz = mObj.getClass();
        for(Field mFie : mClz.getDeclaredFields())
        {
            Class mCls = MField.getSimpleType(mFie.getType());
            mFie.setAccessible(true);
            Object mGetFinal;
            try{
                mGetFinal = Modifier.isStatic(mFie.getModifiers()) ? mFie.get(null) : mFie.get(mObj);
            }
            catch (Exception e)
            {
                continue;
            }
            String PrintValue = mFie.getName()+":"+mCls.getName()+"->"+mGetFinal;
            MLogCat.Print_Debug(PrintValue);
        }
    }
    public static void PrintAllField_Count(Object mObj,int Count)
    {

        Class mClz = mObj.getClass();
        for(int i=0;i<Count;i++){
            mClz = mClz.getSuperclass();
        }
        for(Field mFie : mClz.getDeclaredFields())
        {
            Class mCls = MField.getSimpleType(mFie.getType());
            mFie.setAccessible(true);
            Object mGetFinal;
            try{
                mGetFinal = Modifier.isStatic(mFie.getModifiers()) ? mFie.get(null) : mFie.get(mObj);
            }
            catch (Exception e)
            {
                continue;
            }
            String PrintValue = mFie.getName()+":"+mCls.getName()+"->"+mGetFinal;
            MLogCat.Print_Debug(PrintValue);
        }
    }
    public static void PrintBundle(Bundle bundle)
    {
        Set<String> s = bundle.keySet();
        for(String sKey : s)
        {
            Object Values = bundle.get(sKey);
            MLogCat.Print_Debug(sKey+":"+Values);
        }
    }
    public static void PrintAllField2(Object mObj)
    {

        Class mClz = mObj.getClass().getSuperclass();
        for(Field mFie : mClz.getDeclaredFields())
        {
            Class mCls = MField.getSimpleType(mFie.getType());
            mFie.setAccessible(true);
            Object mGetFinal;
            try{
                mGetFinal = Modifier.isStatic(mFie.getModifiers()) ? mFie.get(null) : mFie.get(mObj);
            }
            catch (Exception e)
            {
                continue;
            }
            String PrintValue = mFie.getName()+":"+mCls.getName()+"->"+mGetFinal;
            MLogCat.Print_Debug(PrintValue);
        }
    }

}
