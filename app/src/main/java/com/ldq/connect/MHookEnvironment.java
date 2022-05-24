package com.ldq.connect;

import android.content.Context;

import com.ldq.Utils.MTaskThread;

public class MHookEnvironment
{
    public static ClassLoader mLoader;

    public static String MVersion;
    public static int MVersionInt;


    public static Context MAppContext;
    public static Object AppInterface;
    public static Object CurrentSession;
    public static MTaskThread mTask = new MTaskThread();
    public static int UserStatus;

    public static String RndToken;

    public static String AppPath;
    public static String PublicStorageModulePath;
    public static Context CacheActContext;

    public static String ServerRoot_CDN;
    public static String ServerRoot_API;

    public static String ProcessName;


}
